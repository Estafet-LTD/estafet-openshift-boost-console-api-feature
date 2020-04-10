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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.estafet.openshift.boost.commons.lib.env.ENV;
import com.estafet.openshift.boost.console.api.feature.dao.CommitDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.message.GitCommit;
import com.estafet.openshift.boost.console.api.feature.message.GitTag;
import com.estafet.openshift.boost.console.api.feature.model.CommitDate;
import com.estafet.openshift.boost.console.api.feature.model.Repo;

@Service
public class GitService {

	private static final Logger log = LoggerFactory.getLogger(GitService.class);

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private RepoDAO repoDAO;
	
	@Autowired
	private CommitDAO commitDAO;

	@Transactional
	public List<GitCommit> getLastestRepoCommits(String repoId) {
		Repo repo = repoDAO.getRepo(repoId);
		List<GitCommit> commits = getRepoCommits(repoId, repo.getLastDate());
		repo.setLastDate(commits.get(0).getCommit().getCommitter().getDate());
		repoDAO.updateRepo(repo);
		updateCommitDates(repo, commits);
		return commits;
	}
	
	private void updateCommitDates(Repo repo, List<GitCommit> commits) {
		for (GitCommit commit : commits) {
			commitDAO.createCommitDate(commit.getCommitDate(repo));
		}
	}

	public List<GitCommit> getRepoCommits(String repo) {
		return getRepoCommits(repo, null);
	}
	
	public List<GitCommit> getRepoCommits(String repo, String since) {
		List<GitCommit> commits = new ArrayList<GitCommit>();
		List<GitCommit> pageCommits = new ArrayList<GitCommit>();
		int page = 1;
		do {
			pageCommits = getRepoCommitsByPage(repo, since, page);
			commits.addAll(pageCommits);
			page++;
		} while (!pageCommits.isEmpty());
		return commits;
	}
	
	private List<GitCommit> getRepoCommitsByPage(String repo, String since, int page) {
		String url = "https://api.github.com/repos/" + ENV.GITHUB + "/" + repo + "/commits?page=" + page;
		if (since != null) {
			url += "&since=" + since; 
		}
		log.info(url);
		return Arrays.asList(restTemplate.getForObject(url, GitCommit[].class));
	}

	public String getVersionForCommit(String repo, String commitId) {
		GitTag[] gitTags = getGitTags(repo);
		Map<String, Set<String>> commits = getGitCommitsByTags(repo);
		for (GitTag gitTag : gitTags) {
			if (commits.get(gitTag.getName()).contains(commitId)) {
				return gitTag.getName();
			}
		}
		throw new RuntimeException("Cannot find version for commit id " + commitId + " in repo " + repo);
	}

	public Map<String, Set<String>> getGitCommitsByTags(String repo) {
		GitTag[] gitTags = getGitTags(repo);
		Map<String, String> commitTagMap = commitTagMap(gitTags);
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		Set<String> commitSet = null;
		for (CommitDate commit : commitDAO.getCommtDatesByRepo(repo)) {
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

	public GitTag[] getGitTags(String repo) {
		String url = "https://api.github.com/repos/" + ENV.GITHUB + "/" + repo + "/tags";
		log.info(url);
		return restTemplate.getForObject(url, GitTag[].class);
	}

}
