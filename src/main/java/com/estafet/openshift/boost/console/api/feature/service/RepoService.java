package com.estafet.openshift.boost.console.api.feature.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.model.BuildApp;
import com.estafet.openshift.boost.console.api.feature.model.BuildEnv;
import com.estafet.openshift.boost.console.api.feature.model.ProdApp;
import com.estafet.openshift.boost.console.api.feature.model.ProdEnv;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.TestApp;
import com.estafet.openshift.boost.console.api.feature.model.TestEnv;
import com.estafet.openshift.boost.console.api.feature.openshift.BuildConfigParser;
import com.estafet.openshift.boost.console.api.feature.openshift.OpenShiftClient;
import com.estafet.openshift.boost.console.api.feature.variables.EnvVars;
import com.openshift.restclient.model.IBuildConfig;

@Service
public class RepoService {

	@Autowired
	private OpenShiftClient client;
	
	@Autowired
	private RepoDAO repoDAO;
	
	@Transactional
	public void updateRepo(BuildEnv buildEnv) {
		for (BuildApp app : buildEnv.getBuildApps()) {
			updateRepo("build", app.getName());
		}
	}
	
	@Transactional
	public void updateRepo(TestEnv testEnv) {
		for (TestApp app : testEnv.getTestApps()) {
			updateRepo("test", app.getName());
		}
	}
	
	@Transactional
	public void updateRepo(ProdEnv prodEnv) {
		for (ProdApp app : prodEnv.getProdApps()) {
			updateRepo("prod", app.getName());
		}
	}

	private void updateRepo(String env, String app) {
		IBuildConfig buildConfig = client.getBuildConfig(env, app);
		String repoId = getRepo(buildConfig);
		Repo repo = repoDAO.getRepo(repoId);
		if (repo == null) {
			repo = new Repo();
			repo.setName(repoId);
			repo.setMicroservice(app);
			repoDAO.create(repo);
		}
	}
	
	private String getRepo(IBuildConfig buildConfig) {
		String repoUrl = new BuildConfigParser(buildConfig).getGitRepository();
		String githubUri = Pattern.quote("https://github.com/");
		String githubOrg = Pattern.quote(EnvVars.getGithub() + "/");
		Pattern r = Pattern.compile("(" + githubUri + ")(" + githubOrg + ")(*)");
		Matcher m = r.matcher(repoUrl); 
		return m.group(2);
	}

}
