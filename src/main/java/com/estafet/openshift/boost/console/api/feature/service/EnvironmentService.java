package com.estafet.openshift.boost.console.api.feature.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
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
		updateRepos(envMessage);
		Env env = envDAO.getEnv(envMessage.getName());
		if (env == null) {
			env = new Env();
			env.setLive(envMessage.isLive());
			env.setName(envMessage.getName());
			env.setUpdatedDate(DateUtils.newDate());
			envDAO.createEnv(env);
		}
		if (!env.getUpdatedDate().equals(envMessage.getUpdatedDate())) {
			updateMicroservices(env, envMessage.getApps());
			updateFeatures(env);
		}
	}

	@Transactional(readOnly = true)
	public EnvironmentDTO getEnv(String env) {
		return envDAO.getEnv(env).getEnvironmentDTO();
	}
	
	private void updateMicroservices(Env env, List<BaseApp> apps) {
		Map<String, Repo> reposMap = repoDAO.microservicesReposMap();
		for (BaseApp app : apps) {
			EnvMicroservice envMicroservice = EnvMicroservice.builder().setDeployedDate(app.getDeployedDate())
					.setMicroservice(app.getName()).setRepo(reposMap.get(app.getName())).setVersion(app.getVersion())
					.build();
			env.addMicroservice(envMicroservice);
		}
		envDAO.updateEnv(env);
	}

	private void updateFeatures(Env env) {
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

	private void updateRepo(BaseEnv env, BaseApp app) {
		IBuildConfig buildConfig = client.getBuildConfig(env.getName(), app.getName());
		String repoId = getRepo(buildConfig);
		Repo repo = repoDAO.getRepo(repoId);
		if (repo == null) {
			repo = new Repo();
			repo.setName(repoId);
			repo.setMicroservice(app.getName());
			repoDAO.create(repo);
		}
	}

	private void updateRepos(BaseEnv env) {
		log.info("updateRepos for env - " + env.getName());
		for (BaseApp app : env.getApps()) {
			updateRepo(env, app);
		}
	}

}
