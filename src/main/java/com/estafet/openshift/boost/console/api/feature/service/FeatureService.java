package com.estafet.openshift.boost.console.api.feature.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.estafet.openshift.boost.console.api.feature.dao.CommitDAO;
import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.dao.EnvFeatureDAO;
import com.estafet.openshift.boost.console.api.feature.dao.FeatureDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.jms.NewEnvironmentFeatureProducer;
import com.estafet.openshift.boost.console.api.feature.message.EnvFeatureMessage;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;
import com.estafet.openshift.boost.console.api.feature.model.EnvMicroservice;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.Matched;
import com.estafet.openshift.boost.console.api.feature.model.MatchedBuilder;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.Version;
import com.estafet.openshift.boost.messages.environments.Environment;
import com.estafet.openshift.boost.messages.features.FeatureMessage;

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
	private CommitDAO commitDAO;
	
	@Autowired
	private EnvFeatureDAO envFeatureDAO;

	@Autowired
	private NewEnvironmentFeatureProducer newEnvFeatureProducer;

	@Transactional
	public void processFeature(FeatureMessage message) {
		log.info(message.toJSON());
		Feature feature = featureDAO.getFeatureById(message.getFeatureId());
		if (feature == null) {
			feature = createFeature(message);
			featureDAO.create(feature);
		}
		Repo repo = repoDAO.getRepo(message.getRepo());
		if (!getRepoFeatures(message).contains(feature)) {
			String version = githubService.getVersionForCommit(repo.getName(), message.getCommitId());
			Matched matched = new MatchedBuilder()
					.setFeature(feature)
					.setSha(message.getCommitId())
					.setVersion(version)
					.setRepo(repo)
					.build();
			commitDAO.createRepoCommit(matched);
		}
		updateEnvs();
	}

	private void updateEnvs() {
		for (Env env : envDAO.getEnvs()) {
			env.setUpdatedDate(DateUtils.newDate());
			envDAO.updateEnv(env);
		}
	}
	
	private Set<Feature> getRepoFeatures(FeatureMessage message) {
		return new HashSet<Feature>(featureDAO.getFeaturesByRepo(message.getRepo(), message.getCommitId()));
	}

	private Feature createFeature(FeatureMessage message) {
		Feature feature = Feature.builder()
				.setDescription(message.getDescription())
				.setFeatureId(message.getFeatureId())
				.setStatus(message.getStatus().getValue())
				.setTitle(message.getTitle()).build();
		return feature;
	}

	@Transactional
	public void processEnvFeature(EnvFeatureMessage envFeatureMessage) {
		if (!envFeatureMessage.getEnvironment().equals("build")) {
			Env env = envDAO.getEnv(envFeatureMessage.getEnvironment());
			Env prevEnv = envDAO.getEnv(env.getPreviousEnv());
			prevEnv.setUpdatedDate(DateUtils.newDate());
			EnvFeature envFeature = prevEnv.getEnvFeature(envFeatureMessage.getFeatureId());
			envFeature.setMigratedDate(envFeatureMessage.getDeployedDate());
			envFeatureDAO.createEnv(envFeature);
		}
	}

	@Transactional
	public void updateEnvFeatures(Environment envMessage) {
		log.info("update EnvFeatures for env - " + envMessage.getName());
		Env env = envDAO.getEnv(envMessage.getName());
		for (EnvMicroservice envMicroservice : env.getMicroservices()) {
			for (Matched matched : commitDAO.getMatchedForMicroservice(envMicroservice.getMicroservice())) {
				Feature feature = matched.getFeature();
				if (!env.getFeatures().contains(feature)) {
					Version matchedVersion = new Version(matched.getVersion());
					Version microserviceVersion = new Version(envMicroservice.getVersion());
					if (envMessage.getName().equals("build") || matchedVersion.isLessThanOrEqual(microserviceVersion)) {
						EnvFeature envFeature =  EnvFeature.builder()
										.setFeature(feature)
										.setDeployedDate(envMicroservice.getDeployedDate())
										.setEnv(env)
										.build();
						envFeatureDAO.createEnv(envFeature);
						newEnvFeatureProducer.sendMessage(envFeature.getEnvFeatureMessage());
					}
				}
			}
		}
		env.setUpdatedDate(envMessage.getUpdatedDate());
		envDAO.updateEnv(env);
	}

}
