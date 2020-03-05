package com.estafet.openshift.boost.console.api.feature.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.message.BaseApp;
import com.estafet.openshift.boost.console.api.feature.message.BaseEnv;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.openshift.BuildConfigParser;
import com.estafet.openshift.boost.console.api.feature.openshift.OpenShiftClient;
import com.estafet.openshift.boost.console.api.feature.util.EnvUtil;
import com.estafet.openshift.boost.console.api.feature.util.RepoUtil;
import com.openshift.restclient.model.IBuildConfig;

@Service
public class RepoService {
	
	private static final Logger log = LoggerFactory.getLogger(RepoService.class);
	
	@Autowired
	private OpenShiftClient client;
	
	@Autowired
	private RepoDAO repoDAO;
	
	@Transactional
	public void updateRepos(BaseEnv env) {
		log.info("updateRepos for env - " + env.getName());
		for (BaseApp app : env.getApps()) {
			updateRepo(env, app);
		}
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
	
	private String getRepo(IBuildConfig buildConfig) {
		return RepoUtil.getRepoFromURL(
				EnvUtil.getGithub(),
				new BuildConfigParser(buildConfig).getGitRepository());
	}

}
