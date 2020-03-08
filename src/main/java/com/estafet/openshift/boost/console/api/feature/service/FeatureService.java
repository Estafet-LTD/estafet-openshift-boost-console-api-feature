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
import com.estafet.openshift.boost.console.api.feature.dao.FeatureDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.jms.NewEnvironmentFeatureProducer;
import com.estafet.openshift.boost.console.api.feature.message.BaseEnv;
import com.estafet.openshift.boost.console.api.feature.message.EnvFeatureMessage;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;
import com.estafet.openshift.boost.console.api.feature.model.EnvMicroservice;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.Matched;
import com.estafet.openshift.boost.console.api.feature.model.MatchedBuilder;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.Version;
import com.estafet.openshift.boost.console.api.feature.util.EnvUtil;
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
	private CommitDAO commitDAO;

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
			String version = githubService.getVersionForCommit(EnvUtil.getGithub(), repo.getName(),
					message.getCommitId());
			Matched matched = new MatchedBuilder()
					.setFeature(feature)
					.setSha(message.getCommitId())
					.setVersion(version)
					.build();
			repo.addCommit(matched);
			updateRepo(repo);
		}
	}
	
	private Set<Feature> getRepoFeatures(FeatureMessage message) {
		return new HashSet<Feature>(featureDAO.getFeaturesByRepo(message.getRepo(), message.getCommitId()));
	}

	private void updateRepo(Repo repo) {
		repoDAO.updateRepo(repo);
		for (Env env : envDAO.getEnvs()) {
			env.setUpdatedDate(DateUtils.newDate());
			envDAO.updateEnv(env);
		}
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
			envDAO.updateEnv(prevEnv);
		}
	}

	@Transactional
	public void updateFeatures(BaseEnv envMessage) {
		log.info("updateFeatures for env - " + envMessage.getName());
		Env env = envDAO.getEnv(envMessage.getName());
		for (EnvMicroservice envMicroservice : env.getMicroservices()) {
			for (Matched matched : commitDAO.getMatchedForMicroservice(envMicroservice.getMicroservice())) {
				Feature feature = matched.getFeature();
				if (!env.getFeatures().contains(feature)) {
					Version matchedVersion = new Version(matched.getVersion());
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

	private EnvFeature createEnvFeature(EnvMicroservice envMicroservice, Feature feature) {
		return EnvFeature.builder()
				.setFeature(feature)
				.setDeployedDate(envMicroservice.getDeployedDate())
				.build();
	}

}
