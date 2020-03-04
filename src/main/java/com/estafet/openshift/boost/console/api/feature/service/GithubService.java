package com.estafet.openshift.boost.console.api.feature.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.estafet.openshift.boost.console.api.feature.message.GitCommit;
import com.estafet.openshift.boost.console.api.feature.message.GitTag;
import com.estafet.openshift.boost.console.api.feature.variables.EnvVars;

@Service
public class GithubService {

	private static final Logger log = LoggerFactory.getLogger(GithubService.class);

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
		String url = "https://api.github.com/repos/" + EnvVars.getGithub() + "/" + repo + "/commits?page=" + page;
		log.info(url);
		return Arrays.asList(restTemplate.getForObject(url, GitCommit[].class));
	}

	public String getVersionForCommit(String repo, String commitId) {
		Map<String, List<GitCommit>> commits = getGitCommitsByTags(repo);
		for (String version : commits.keySet()) {
			for (GitCommit commit : commits.get(version)) {
				if (commit.getSha().equals(commitId)) {
					return version;
				}
			}
		}
		return null;
	}

	public Map<String, List<GitCommit>> getGitCommitsByTags(String repo) {
		Map<String, List<GitCommit>> map = new HashMap<String, List<GitCommit>>();
		List<GitCommit> commits = getRepoCommits(repo);
		List<GitTag> tags = getGitTags(repo);
		for (GitTag tag : tags) {
			map.put(tag.getName(), subList(tag, commits));
		}
		return map;
	}

	private List<GitCommit> subList(GitTag tag, List<GitCommit> commits) {
		for (int i = 0; i < commits.size(); i++) {
			if (commits.get(i).getSha().equals(tag.getCommit().getSha())) {
				return commits.subList(i, commits.size());
			}
		}
		return null;
	}

	private List<GitTag> getGitTags(String repo) {
		String url = "https://api.github.com/repos/" + EnvVars.getGithub() + "/" + repo + "/tags";
		log.info(url);
		return Arrays.asList(restTemplate.getForObject(url, GitTag[].class));
	}

}
