package com.estafet.openshift.boost.console.api.feature.service;

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
import com.estafet.openshift.boost.console.api.feature.jms.EnvConsumer;
import com.estafet.openshift.boost.console.api.feature.jms.NewEnvironmentFeatureProducer;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;
import com.estafet.openshift.boost.console.api.feature.model.EnvMicroservice;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.Matched;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.Version;
import com.estafet.openshift.boost.console.api.feature.openshift.BuildConfigParser;
import com.estafet.openshift.boost.console.api.feature.openshift.OpenShiftClient;
import com.estafet.openshift.boost.console.api.feature.util.ENV;
import com.estafet.openshift.boost.console.api.feature.util.RepoUtil;
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

	@Autowired
	private NewEnvironmentFeatureProducer newEnvFeatureProducer;

	@Transactional
	public void processEnvMessage(Environment envMessage) {
		EnvConsumer.log.info("env - " + envMessage.getName());
		if (createEnv(envMessage)) {
			updateRepos(envMessage);
			updateMicroservices(envMessage);
			updateEnvFeatures(envMessage);
		}
	}
	
	private boolean createEnv(Environment envMessage) {
		Env env = envDAO.getEnv(envMessage.getName());
		if (env == null) {
			env = Env.builder()
					.setLive(envMessage.isLive())
					.setUpdatedDate(envMessage.getUpdatedDate())
					.setName(envMessage.getName())
					.build();
			envDAO.createEnv(env);
			log.info("created env - " + envMessage.getName());
			return true;
		} else {
			log.info("already exists env - " + envMessage.getName());
			return !env.getUpdatedDate().equals(envMessage.getUpdatedDate());
		}
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
		return RepoUtil.getRepoFromURL(
				ENV.getGithub(),
				new BuildConfigParser(buildConfig).getGitRepository());
	}
	
	private void updateEnvFeatures(Environment envMessage) {
		log.info("update EnvFeatures for env - " + envMessage.getName());
		log.debug(envMessage.toJSON());
		Env env = envDAO.getEnv(envMessage.getName());
		for (EnvMicroservice envMicroservice : env.getMicroservices()) {
			for (Matched matched : commitDAO.getMatchedForMicroservice(envMicroservice.getMicroservice())) {
				Feature feature = matched.getFeature();
				if (!env.getFeatures().contains(feature)) {
					Version matchedVersion = new Version(matched.getVersion());
					Version microserviceVersion = new Version(envMicroservice.getVersion());
					if (envMessage.getName().equals("build") || (matchedVersion.isLessThanOrEqual(microserviceVersion)
							&& feature.getStatus().equals("Done"))) {
						EnvFeature envFeature = EnvFeature.builder()
								.setFeature(feature)
								.setDeployedDate(envMicroservice.getDeployedDate())
								.setEnv(env)
								.build();
						envFeatureDAO.save(envFeature);
						newEnvFeatureProducer.sendMessage(envFeature.getEnvFeatureMessage());
					}
				}
			}
		}
		env.setUpdatedDate(envMessage.getUpdatedDate());
		envDAO.updateEnv(env);
	}

	@Transactional(readOnly = true)
	public EnvironmentDTO getEnv(String env) {
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

}
