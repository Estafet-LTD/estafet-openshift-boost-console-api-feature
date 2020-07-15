package com.estafet.openshift.boost.console.api.feature.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.CommitDAO;
import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.dao.EnvFeatureDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.dto.EnvironmentDTO;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;
import com.estafet.openshift.boost.console.api.feature.model.EnvMicroservice;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.PromoteStatus;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.RepoCommit;
import com.estafet.openshift.boost.console.api.feature.model.Version;
import com.estafet.openshift.boost.console.api.feature.openshift.BuildConfigParser;
import com.estafet.openshift.boost.console.api.feature.openshift.OpenShiftClient;
import com.estafet.openshift.boost.messages.environments.Environment;
import com.estafet.openshift.boost.messages.environments.EnvironmentApp;
import com.openshift.restclient.model.IBuildConfig;

@Service
public class EnvironmentService {

	private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);

	@Autowired
	private OpenShiftClient client;

	@Autowired
	private EnvDAO envDAO;

	@Autowired
	private RepoDAO repoDAO;

	@Autowired
	private CommitDAO commitDAO;

	@Autowired
	private EnvFeatureDAO envFeatureDAO;

	@Transactional
	public void processEnvMessage(Environment envMessage) {
		log.info("env - " + envMessage.getName());
		if (updateEnv(envMessage)) {
			updateRepos(envMessage);
			updateMicroservices(envMessage);
			updateEnvFeatures(envMessage);
		}
		updateMigrationDate(envMessage);
		updatePromoteStatus(envMessage);
	}

	private void updatePromoteStatus(Environment envMessage) {
		Env env = envDAO.getEnv(envMessage.getName());
		log.info("env - " + env.toString());
		if (env.getName().equals("build")) {
			for (EnvFeature envFeature : env.getEnvFeatures()) {
				if (envFeature.getMigratedDate() != null
						&& PromoteStatus.valueOf(envFeature.getPromoteStatus()) != PromoteStatus.FULLY_PROMOTED) {
					envFeature.setPromoteStatus(PromoteStatus.FULLY_PROMOTED.getValue());
					envFeatureDAO.update(envFeature);
				}
			}
		}
		if (env.isLive()) {
			for (EnvFeature envFeature : envFeatureDAO.getUnResolvedEnvFeatures(env.getName())) {
				envFeature.setPromoteStatus(PromoteStatus.FULLY_PROMOTED.getValue());
				envFeatureDAO.update(envFeature);
			}
		} else {
			Env nextEnv = nextUnResolvedEnv(env);
			if (nextEnv != null) {
				log.info("nextEnv - " + nextEnv.toString());
				for (EnvFeature nextEnvFeature : envFeatureDAO.getUnResolvedEnvFeatures(nextEnv.getName())) {
					log.info("nextEnvFeature - " + nextEnvFeature.toString());
					EnvFeature envFeature = env.getEnvFeature(nextEnvFeature.getFeature().getFeatureId());
					log.info("envFeature - " + envFeature);
					if (envFeature != null) {
						nextEnvFeature.updatePromoteStatus(envFeature);
						envFeatureDAO.update(nextEnvFeature);
					}
				}
			}			
		}
	}

	private Env nextUnResolvedEnv(Env env) {
		log.info("nextUnResolvedEnv" + env.toString());
		if (env.isPreProd()) {
			return envDAO.getStagingEnv();
		} else if (env.isStaging()) {
			return envDAO.getLiveEnv();
		} else if (!env.isLive()) {
			return envDAO.getEnv(env.getNext());
		}
		log.info("nothing for" + env.toString());
		return null;
	}

	private void updateMigrationDate(Environment envMessage) {
		Env env = envDAO.getEnv(envMessage.getName());
		log.info("env - " + env.toString());
		Env nextEnv = nextMigratedEnv(env);
		if (nextEnv != null) {
			log.info("nextEnv - " + nextEnv.toString());
			List<EnvFeature> envFeatures = envFeatureDAO.getNewEnvFeatures(envMessage.getName());
			for (EnvFeature envFeature : envFeatures) {
				log.info("envFeature - " + envFeature.toString());
				EnvFeature nextEnvFeature = nextEnv.getEnvFeature(envFeature.getFeature().getFeatureId());
				log.info("nextEnvFeature - " + nextEnvFeature);
				if (nextEnvFeature != null) {
					envFeature.setMigratedDate(nextEnvFeature.calculateDeployedDate());
					envFeatureDAO.update(envFeature);
				}
			}
		}
	}

	private Env nextMigratedEnv(Env env) {
		log.info(env.toString());
		if (env.getNext() != null) {
			if (env.getNext().equals("prod")) {
				return envDAO.getStagingEnv();
			} else {
				return envDAO.getEnv(env.getNext());
			}
		} else if (env.getName().equals("green") || env.getName().equals("blue")) {
			return envDAO.getLiveEnv();
		}
		return null;
	}

	private boolean updateEnv(Environment envMessage) {
		Env env = envDAO.getEnv(envMessage.getName());
		if (env == null) {
			envDAO.createEnv(Env.getEnv(envMessage));
			log.info("created env - " + envMessage.getName());
			return true;
		} else if (!env.getUpdatedDate().equals(envMessage.getUpdatedDate())) {
			log.info("env changed - " + envMessage.getName());
			envDAO.updateEnv(env.merge(Env.getEnv(envMessage)));
			return true;
		}
		return false;
	}

	private void updateRepos(Environment env) {
		log.info("updateRepos for env - " + env.getName());
		for (EnvironmentApp app : env.getApps()) {
			updateRepo(env, app);
		}
		log.info("Repos successfully updated for env - " + env.getName());
	}

	private Repo updateRepo(Environment env, EnvironmentApp app) {
		IBuildConfig buildConfig = client.getBuildConfig(app.getName());
		String repoUrl = new BuildConfigParser(buildConfig).getGitRepository();
		Repo repo = repoDAO.getRepoByURL(repoUrl);
		if (repo == null) {
			repo = Repo.builder().setUrl(repoUrl).setMicroservice(app.getName()).build();
			repoDAO.createRepo(repo);
		}
		return repo;
	}

	private void updateEnvFeatures(Environment envMessage) {
		log.info("update EnvFeatures for env - " + envMessage.getName());
		Env env = envDAO.getEnv(envMessage.getName());
		for (EnvMicroservice envMicroservice : env.getMicroservices()) {
			for (RepoCommit matched : commitDAO.getMatchedForMicroservice(envMicroservice.getMicroservice())) {
				Feature feature = matched.getFeature();
				if (isEnvFeature(env, feature, matched, envMicroservice)) {
					if (!env.getFeatures().contains(feature)) {
						EnvFeature envFeature = EnvFeature.builder().setFeature(feature)
								.setDeployedDate(envMicroservice.getDeployedDate()).setEnv(env).build();
						envFeatureDAO.create(envFeature);
					}
				}
			}
		}
		env.setUpdatedDate(envMessage.getUpdatedDate());
		envDAO.updateEnv(env);
	}

	private boolean isEnvFeature(Env env, Feature feature, RepoCommit matched, EnvMicroservice envMicroservice) {
		Version matchedVersion = new Version(matched.getTag());
		Version microserviceVersion = new Version(envMicroservice.getVersion());
		if (matchedVersion.isLessThanOrEqual(microserviceVersion)) {
			if (env.getName().equals("build") || env.getName().equals("test")) {
				return true;
			} else if (feature.getStatus().equals("Done")) {
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public EnvironmentDTO getEnvironment(String env) {
		return envDAO.getEnv(env).getEnvironmentDTO();
	}

	@Transactional
	public void updateMicroservices(Environment envMessage) {
		log.info("updateMicroservices for env - " + envMessage.getName());
		Env env = envDAO.getEnv(envMessage.getName());
		for (EnvironmentApp app : envMessage.getApps()) {
			log.info("create envMicroservice for " + app.getName());
			env.updateMicroservice(app, repoDAO.getRepoByMicroservice(app.getName()));
		}
		env.setUpdatedDate(envMessage.getUpdatedDate()); // reset the date
		envDAO.updateEnv(env);
	}

	@Transactional(readOnly = true)
	public List<EnvironmentDTO> getEnvironments() {
		return getEnvironments("build");
	}

	public List<EnvironmentDTO> getEnvironments(String envId) {
		return getEnvironments(envId, new ArrayList<EnvironmentDTO>());
	}

	private List<EnvironmentDTO> getEnvironments(String envId, List<EnvironmentDTO> envs) {
		Env env = envDAO.getEnv(envId);
		envs.add(env.getEnvironmentDTO());
		if (!env.getNext().equals("prod")) {
			return getEnvironments(env.getNext(), envs);
		} else {
			Env green = envDAO.getEnv("green");
			Env blue = envDAO.getEnv("blue");
			if (green.getLive()) {
				envs.add(blue.getEnvironmentDTO());
				envs.add(green.getEnvironmentDTO());
			} else {
				envs.add(green.getEnvironmentDTO());
				envs.add(blue.getEnvironmentDTO());
			}
			return envs;
		}
	}

}
