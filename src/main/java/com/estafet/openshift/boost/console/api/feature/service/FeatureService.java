package com.estafet.openshift.boost.console.api.feature.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.dao.FeatureDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.message.BaseEnv;
import com.estafet.openshift.boost.console.api.feature.message.GitCommit;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.Matched;
import com.estafet.openshift.boost.console.api.feature.model.MatchedBuilder;
import com.estafet.openshift.boost.console.api.feature.model.Microservice;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.Version;
import com.estafet.openshift.boost.messages.model.FeatureMessage;

public class FeatureService {

	@Autowired
	private GithubService githubService;

	@Autowired
	private EnvDAO envDAO;
		
	@Autowired
	private FeatureDAO featureDAO;
	
	@Autowired
	private RepoDAO repoDAO;

	@Transactional
	public void updateFeatures(BaseEnv baseEnv) {
		Env env = envDAO.getEnv(baseEnv.getName());
		for (Microservice microservice : env.getMicroservices()) {
			for (Feature feature : featureDAO.getFeaturesByRepo(microservice.getRepo())) {
				if (!env.getFeatures().contains(feature)) {
					for (Matched matched : feature.getMatched()) {
						Version matchedVersion = new Version(matched.getVersion());
						Version microserviceVersion = new Version(microservice.getVersion());
						if (matchedVersion.isLessThanOrEqual(microserviceVersion)) {
							env.addEnvFeature(createEnvFeature(microservice, feature));
						}
					}
				}
			}
		}
		envDAO.updateEnv(env);
	}

	private EnvFeature createEnvFeature(Microservice microservice, Feature feature) {
		return EnvFeature.builder()
				.setFeature(feature)
				.setDeployedDate(microservice.getDeployedDate())
				.build();
	}
	
	@Transactional
	public void processFeature(FeatureMessage featureMessage) {
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


}
