package com.estafet.openshift.boost.console.api.feature.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.estafet.openshift.boost.commons.lib.rest.RestHelper;
import com.estafet.openshift.boost.console.api.feature.model.GitCommit;
import com.estafet.openshift.boost.console.api.feature.variables.EnvVars;

public class GithubService {

	@Autowired
	private RestTemplate restTemplate;

	public List<GitCommit> getRepoCommits(String repo) {
		List<GitCommit> commits = new ArrayList<GitCommit>();
		List<GitCommit> pageCommits = new ArrayList<GitCommit>();
		int page = 1;
		do {
			pageCommits = getRepoCommits(repo, page);
			commits.addAll(pageCommits);
			page++;
		} while (!pageCommits.isEmpty());
		return commits;
	}

	private List<GitCommit> getRepoCommits(String repo, int page) {
		return RestHelper.getRestQuery(restTemplate,
				"https://api.github.com/repos/" + EnvVars.getGithub() + "/" + repo + "/commits?page=" + page,
				GitCommit.class);
	}

	public GitCommit getCommit(String repo, String commitId) {
		return null;
	}

	public List<GitCommit> getVersionCommits(String repo, String version) {
		return null;
	}

}
