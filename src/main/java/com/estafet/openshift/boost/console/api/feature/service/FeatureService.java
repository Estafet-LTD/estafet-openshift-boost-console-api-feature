package com.estafet.openshift.boost.console.api.feature.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.Microservice;
import com.estafet.openshift.boost.console.api.feature.model.VersionCommits;
import com.estafet.openshift.boost.messages.model.FeatureMessage;

public class FeatureService {

	@Autowired
	private GithubService githubService;

	@Autowired
	private EnvDAO envDAO;

	@Transactional
	public void processFeature(FeatureMessage featureMessage) {
		for (Env env : envDAO.getEnvs()) {
			for (Microservice microservice : env.getMicroservices()) {
				VersionCommits versionCommits = getVersionCommits(microservice);
				if (versionCommits.isIn(featureMessage.getCommitId())) {
					env.addFeature(createFeature(featureMessage, microservice));
				}
			}
			envDAO.updateEnv(env);
		}
	}

	private Feature createFeature(FeatureMessage featureMessage, Microservice microservice) {
		return Feature.builder()
				.setDeployedDate(microservice.getDeployedDate())
				.setDescription(featureMessage.getDescription())
				.setFeatureId(featureMessage.getFeatureId())
				.setStatus(featureMessage.getStatus().getValue())
				.setTitle(featureMessage.getTitle())
				.build();
	}
	
	private VersionCommits getVersionCommits(Microservice microservice) {
		return new VersionCommits(githubService.getVersionCommits(microservice.getRepo(), microservice.getVersion()));
	}

}
