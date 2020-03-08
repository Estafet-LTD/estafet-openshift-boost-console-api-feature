package com.estafet.openshift.boost.console.api.feature.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.openshift.BuildConfigParser;
import com.estafet.openshift.boost.console.api.feature.openshift.OpenShiftClient;
import com.estafet.openshift.boost.console.api.feature.util.ENV;
import com.estafet.openshift.boost.console.api.feature.util.RepoUtil;
import com.estafet.openshift.boost.messages.environments.Environment;
import com.estafet.openshift.boost.messages.environments.EnvironmentApp;
import com.openshift.restclient.model.IBuildConfig;

@Service
public class RepositoryService {

	private static final Logger log = LoggerFactory.getLogger(RepositoryService.class);
	
	@Autowired
	private OpenShiftClient client;
	
	@Autowired
	private RepoDAO repoDAO;

	@Transactional
	public void updateRepos(Environment env) {
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

}
