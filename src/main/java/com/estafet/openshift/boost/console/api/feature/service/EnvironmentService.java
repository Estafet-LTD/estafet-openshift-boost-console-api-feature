package com.estafet.openshift.boost.console.api.feature.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.dto.EnvironmentDTO;
import com.estafet.openshift.boost.console.api.feature.jms.NewEnvironmentFeatureProducer;
import com.estafet.openshift.boost.console.api.feature.message.BaseApp;
import com.estafet.openshift.boost.console.api.feature.message.BaseEnv;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;
import com.estafet.openshift.boost.console.api.feature.model.EnvMicroservice;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.Matched;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.RepoCommit;
import com.estafet.openshift.boost.console.api.feature.openshift.BuildConfigParser;
import com.estafet.openshift.boost.console.api.feature.openshift.OpenShiftClient;
import com.estafet.openshift.boost.console.api.feature.util.EnvUtil;
import com.estafet.openshift.boost.console.api.feature.util.RepoUtil;
import com.estafet.openshift.boost.console.api.feature.util.Version;
import com.openshift.restclient.model.IBuildConfig;

@Service
public class EnvironmentService {

	private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);

	@Autowired
	private EnvDAO envDAO;

	@Autowired
	private RepoDAO repoDAO;

	@Autowired
	private NewEnvironmentFeatureProducer newEnvFeatureProducer;

	@Autowired
	private OpenShiftClient client;

	@Transactional
	public void processEnvUpdate(BaseEnv envMessage) {
		log.info("processEnvUpdate for env - " + envMessage.getName());
		Env env = envDAO.getEnv(envMessage.getName());
		if (env == null) {
			env = createEnv(envMessage);
		}
		if (!env.getUpdatedDate().equals(envMessage.getUpdatedDate())) {
			Map<String, Repo> reposMap = updateRepos(envMessage);
			updateMicroservices(env, envMessage.getApps(), reposMap);
			updateFeatures(env);
		}
	}

	private Env createEnv(BaseEnv envMessage) {
		Env env = Env.builder()
				.setLive(envMessage.isLive())
				.setName(envMessage.getName())
				.build();
		envDAO.createEnv(env);
		return env;
	}

	@Transactional(readOnly = true)
	public EnvironmentDTO getEnv(String env) {
		return envDAO.getEnv(env).getEnvironmentDTO();
	}
	
	private void updateMicroservices(Env env, List<BaseApp> apps, Map<String, Repo> reposMap) {
		log.info("updateMicroservices for env - " + env.getName());
		for (BaseApp app : apps) {
			EnvMicroservice.builder()
					.setDeployedDate(app.getDeployedDate())
					.setMicroservice(app.getName())
					.setRepo(reposMap.get(app.getName()))
					.setEnv(env)
					.setVersion(app.getVersion())
					.build();
		}
		envDAO.updateEnv(env);
	}

	private void updateFeatures(Env env) {
		log.info("updateFeatures for env - " + env.getName());
		for (EnvMicroservice envMicroservice : env.getMicroservices()) {
			for (RepoCommit commit : envMicroservice.getRepo().getCommits()) {
				if (commit instanceof Matched) {
					Feature feature = ((Matched) commit).getFeature();
					if (!env.getFeatures().contains(feature)) {
						Version matchedVersion = new Version(commit.getVersion());
						Version microserviceVersion = new Version(envMicroservice.getVersion());
						if (microserviceVersion.isSnapshot() || matchedVersion.isLessThanOrEqual(microserviceVersion)) {
							EnvFeature envFeature = createEnvFeature(envMicroservice, feature);
							env.addEnvFeature(envFeature);
							envDAO.updateEnv(env);
							newEnvFeatureProducer.sendMessage(envFeature.getEnvFeatureMessage());
						}
					}
				}
			}
		}
	}

	private EnvFeature createEnvFeature(EnvMicroservice envMicroservice, Feature feature) {
		return EnvFeature.builder()
				.setFeature(feature)
				.setDeployedDate(envMicroservice.getDeployedDate())
				.build();
	}

	private String getRepo(IBuildConfig buildConfig) {
		return RepoUtil.getRepoFromURL(
				EnvUtil.getGithub(),
				new BuildConfigParser(buildConfig).getGitRepository());
	}

	private Repo updateRepo(BaseEnv env, BaseApp app) {
		IBuildConfig buildConfig = client.getBuildConfig(env.getName(), app.getName());
		String repoId = getRepo(buildConfig);
		Repo repo = repoDAO.getRepo(repoId);
		if (repo == null) {
			repo = new Repo();
			repo.setName(repoId);
			repo.setMicroservice(app.getName());
			repoDAO.create(repo);
		}
		return repo;
	}

	private Map<String, Repo> updateRepos(BaseEnv env) {
		log.info("updateRepos for env - " + env.getName());
		Map<String, Repo> microservices = new HashMap<String, Repo>();
		for (BaseApp app : env.getApps()) {
			microservices.put(app.getName(), updateRepo(env, app));
		}
		return microservices;
	}

}
