package com.estafet.openshift.boost.console.api.feature.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.estafet.openshift.boost.commons.lib.rest.RestHelper;
import com.estafet.openshift.boost.console.api.feature.model.GitCommit;
import com.estafet.openshift.boost.console.api.feature.model.GitTag;
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

	public List<GitCommit> getVersionCommits(String repo, String version) {
		List<GitCommit> commits = getRepoCommits(repo);
		GitTag gitTag = getGitTag(repo, version);
		for (int i = commits.size()-1; i >= 0; i--) {
			if (commits.get(i).getSha().equals(gitTag.getCommit().getSha())) {
				return commits.subList(0, i+1);
			}
		}
		return null;
	}
		
	private GitTag getGitTag(String repo, String version) {
		for (GitTag gitTag : getGitTags(repo)) {
			if (gitTag.getName().equals(version)) {
				return gitTag;
			}
		}
		return null;
	}
	
	private List<GitTag> getGitTags(String repo) {
		return RestHelper.getRestQuery(restTemplate,
				"https://api.github.com/repos/" + EnvVars.getGithub() + "/" + repo + "/tags",
				GitTag.class);
	}

}
