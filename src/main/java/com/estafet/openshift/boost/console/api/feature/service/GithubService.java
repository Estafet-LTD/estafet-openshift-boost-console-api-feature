package com.estafet.openshift.boost.console.api.feature.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.message.GitCommit;
import com.estafet.openshift.boost.console.api.feature.message.GitTag;
import com.estafet.openshift.boost.console.api.feature.model.Repo;

@Service
public class GithubService {

	private static final Logger log = LoggerFactory.getLogger(GithubService.class);

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private RepoDAO repoDAO;

	@Transactional
	public List<GitCommit> getLastestRepoCommits(String github, String repoId) {
		Repo repo = repoDAO.getRepo(repoId);
		List<GitCommit> commits = getRepoCommits(github, repoId, repo.getLastDate());
		repo.setLastDate(commits.get(0).getCommit().getCommitter().getDate());
		repoDAO.updateRepo(repo);
		return commits;
	}

	public List<GitCommit> getRepoCommits(String github, String repo) {
		return getRepoCommits(github, repo, null);
	}
	
	@Cacheable(cacheNames = { "commits" })
	public List<GitCommit> getRepoCommits(String github, String repo, String since) {
		List<GitCommit> commits = new ArrayList<GitCommit>();
		List<GitCommit> pageCommits = new ArrayList<GitCommit>();
		int page = 1;
		do {
			pageCommits = getRepoCommitsByPage(github, repo, since, page);
			commits.addAll(pageCommits);
			page++;
		} while (!pageCommits.isEmpty());
		return commits;
	}
	
	private List<GitCommit> getRepoCommitsByPage(String github, String repo, String since, int page) {
		String url = "https://api.github.com/repos/" + github + "/" + repo + "/commits?page=" + page;
		if (since != null) {
			url += "&since=" + since; 
		}
		log.info(url);
		return Arrays.asList(restTemplate.getForObject(url, GitCommit[].class));
	}

	public String getVersionForCommit(String github, String repo, String commitId) {
		GitTag[] gitTags = getGitTags(github, repo);
		Map<String, Set<String>> commits = getGitCommitsByTags(github, repo, gitTags);
		for (GitTag gitTag : gitTags) {
			if (commits.get(gitTag.getName()).contains(commitId)) {
				return gitTag.getName();
			}
		}
		throw new RuntimeException("Cannot find version for commit id " + commitId + " in repo " + repo);
	}

	@Cacheable(cacheNames = { "tags" })
	public Map<String, Set<String>> getGitCommitsByTags(String github, String repo, GitTag[] gitTags) {
		Map<String, String> commitTagMap = commitTagMap(gitTags);
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		Set<String> commitSet = null;
		for (GitCommit commit : getRepoCommits(github, repo)) {
			if (commitTagMap.get(commit.getSha()) != null) {
				commitSet = new HashSet<String>();
				result.put(commitTagMap.get(commit.getSha()), commitSet);
			}
			if (commitSet == null) {
				log.warn("Cannot find corresponding tag for - commit " + commit.getSha() + " for repo " + repo);
			} else {
				commitSet.add(commit.getSha());	
			}
		}
		return result;
	}
	
	private Map<String, String> commitTagMap(GitTag[] gitTags) {
		Map<String, String> commitTagMap = new HashMap<String, String>();
		for (GitTag gitTag : gitTags) {
			commitTagMap.put(gitTag.getCommit().getSha(), gitTag.getName());
		}
		return commitTagMap; 
	}

	private GitTag[] getGitTags(String github, String repo) {
		String url = "https://api.github.com/repos/" + github + "/" + repo + "/tags";
		log.info(url);
		return restTemplate.getForObject(url, GitTag[].class);
	}

}
