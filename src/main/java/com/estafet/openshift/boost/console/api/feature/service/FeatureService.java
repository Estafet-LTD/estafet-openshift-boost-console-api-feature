package com.estafet.openshift.boost.console.api.feature.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.dao.FeatureDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.jms.NewEnvironmentFeatureProducer;
import com.estafet.openshift.boost.console.api.feature.message.BaseEnv;
import com.estafet.openshift.boost.console.api.feature.message.EnvFeatureMessage;
import com.estafet.openshift.boost.console.api.feature.message.GitCommit;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.Matched;
import com.estafet.openshift.boost.console.api.feature.model.MatchedBuilder;
import com.estafet.openshift.boost.console.api.feature.model.Microservice;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.util.Version;
import com.estafet.openshift.boost.messages.model.FeatureMessage;

@Service
public class FeatureService {

	private static final Logger log = LoggerFactory.getLogger(FeatureService.class);
	
	@Autowired
	private GithubService githubService;

	@Autowired
	private EnvDAO envDAO;
		
	@Autowired
	private FeatureDAO featureDAO;
	
	@Autowired
	private RepoDAO repoDAO;
	
	@Autowired
	private NewEnvironmentFeatureProducer newEnvFeatureProducer;

	@Transactional
	public void updateFeatures(BaseEnv baseEnv) {
		Env env = envDAO.getEnv(baseEnv.getName());
		for (Microservice microservice : env.getMicroservices()) {
			for (Feature feature : featureDAO.getFeaturesByRepo(microservice.getRepo())) {
				if (!env.getFeatures().contains(feature)) {
					for (Matched matched : feature.getMatched()) {
						Version matchedVersion = new Version(matched.getVersion());
						Version microserviceVersion = new Version(microservice.getVersion());
						if (microserviceVersion.isSnapshot() || matchedVersion.isLessThanOrEqual(microserviceVersion)) {
							EnvFeature envFeature = createEnvFeature(microservice, feature);
							env.addEnvFeature(envFeature);
							envDAO.updateEnv(env);
							newEnvFeatureProducer.sendMessage(envFeature.getEnvFeatureMessage());
						}
					}
				}
			}
		}
	}

	private EnvFeature createEnvFeature(Microservice microservice, Feature feature) {
		return EnvFeature.builder()
				.setFeature(feature)
				.setDeployedDate(microservice.getDeployedDate())
				.build();
	}
	
	@Transactional
	public void processFeature(FeatureMessage featureMessage) {
		log.info(featureMessage.toJSON());
		Feature feature = featureDAO.getFeatureById(featureMessage.getFeatureId());
		if (feature == null) {
			feature = createFeature(featureMessage);
			featureDAO.create(feature);
		}
		Repo repo = repoDAO.getRepo(featureMessage.getRepo());
		if (!repo.contains(feature)) {
			for(GitCommit commit : githubService.getRepoCommits(featureMessage.getRepo())) {
				if (commit.getSha().equals(featureMessage.getCommitId())) {
					String version = githubService.getVersionForCommit(repo.getName(), featureMessage.getCommitId());
					Matched matched = new MatchedBuilder()
							.setFeature(feature)
							.setSha(featureMessage.getCommitId())
							.setVersion(version)
							.build();
					repo.addCommit(matched);
				}
			}	
			repoDAO.update(repo);
		}
	}

	private Feature createFeature(FeatureMessage featureMessage) {
		return Feature.builder()
				.setDescription(featureMessage.getDescription())
				.setFeatureId(featureMessage.getFeatureId())
				.setStatus(featureMessage.getStatus().getValue())
				.setTitle(featureMessage.getTitle())
				.build();
	}

	@Transactional
	public void processEnvFeature(EnvFeatureMessage envFeatureMessage) {
		if (!envFeatureMessage.getEnvironment().equals("build")) {
			Env env = envDAO.getEnv(envFeatureMessage.getEnvironment());
			Env prevEnv =  envDAO.getEnv(env.getPreviousEnv());
			prevEnv.setUpdatedDate(DateUtils.newDate());
			EnvFeature envFeature = prevEnv.getEnvFeature(envFeatureMessage.getFeatureId());
			envFeature.setMigratedDate(envFeatureMessage.getDeployedDate());
			envDAO.updateEnv(prevEnv);
		}
	}


}
