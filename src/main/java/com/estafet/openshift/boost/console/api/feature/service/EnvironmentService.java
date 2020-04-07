package com.estafet.openshift.boost.console.api.feature.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.commons.lib.env.ENV;
import com.estafet.openshift.boost.console.api.feature.dao.CommitDAO;
import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.dao.EnvFeatureDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.dto.EnvironmentDTO;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;
import com.estafet.openshift.boost.console.api.feature.model.EnvMicroservice;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.Matched;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
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
	}

	private void updateMigrationDate(Environment envMessage) {
		Env env = envDAO.getEnv(envMessage.getName());
		log.info("env - " + env.toString());
		Env nextEnv = nextEnv(env);
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

	private Env nextEnv(Env env) {
		log.info(env.toString());
		if (env.getNext() != null) {
			if (env.getNext().equals("prod")) {
				return envDAO.getStagingEnv();	
			} else {
				return envDAO.getEnv(env.getNext());	
			}
		} else if (env.getName().equals("green") || env.getName().equals("blue")) {
			return env.getLive() ? envDAO.getLiveEnv() : envDAO.getStagingEnv();
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
		String repoId = getRepo(buildConfig);
		Repo repo = repoDAO.getRepo(repoId);
		if (repo == null) {
			repo = Repo.builder().setName(repoId).setMicroservice(app.getName()).build();
			repoDAO.createRepo(repo);
		}
		return repo;
	}

	private String getRepo(IBuildConfig buildConfig) {
		String repoUrl = new BuildConfigParser(buildConfig).getGitRepository();
		log.info("repoURL - " + repoUrl);
		String githubUri = Pattern.quote("https://github.com/");
		String githubOrg = Pattern.quote(ENV.GITHUB + "/");
		Pattern r = Pattern.compile("(" + githubUri + ")(" + githubOrg + ")(.+)");
		log.info("pattern - " + r.pattern());
		Matcher m = r.matcher(repoUrl); 
		m.find();
		String repo = m.group(3);
		return repo;
	}

	private void updateEnvFeatures(Environment envMessage) {
		log.info("update EnvFeatures for env - " + envMessage.getName());
		Env env = envDAO.getEnv(envMessage.getName());
		for (EnvMicroservice envMicroservice : env.getMicroservices()) {
			for (Matched matched : commitDAO.getMatchedForMicroservice(envMicroservice.getMicroservice())) {
				Feature feature = matched.getFeature();
				if (!env.getFeatures().contains(feature)) {
					Version matchedVersion = new Version(matched.getVersion());
					Version microserviceVersion = new Version(envMicroservice.getVersion());
					if (isNewEnvFeature(env, feature, matchedVersion, microserviceVersion)) {
						EnvFeature envFeature = EnvFeature.builder()
								.setFeature(feature)
								.setDeployedDate(envMicroservice.getDeployedDate())
								.setEnv(env)
								.build();
						envFeatureDAO.create(envFeature);
					}
				}
			}
		}
		env.setUpdatedDate(envMessage.getUpdatedDate());
		envDAO.updateEnv(env);
	}

	private boolean isNewEnvFeature(Env env, Feature feature, Version matchedVersion, Version microserviceVersion) {
		log.info("env - " + env.toString());
		log.info("feature - " + feature.toString());
		log.info("matchedVersion - " + matchedVersion.toString());
		log.info("microserviceVersion - " + microserviceVersion.toString());
		log.info("isLessThanOrEqual - " + matchedVersion.isLessThanOrEqual(microserviceVersion));
		if (matchedVersion.isLessThanOrEqual(microserviceVersion)) {
			if (env.getName().equals("build") || env.getName().equals("test")) {
				log.info("build or test matched");
				return true;
			} else if (feature.getStatus().equals("Done")) {
				log.info("Another env matched");
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
