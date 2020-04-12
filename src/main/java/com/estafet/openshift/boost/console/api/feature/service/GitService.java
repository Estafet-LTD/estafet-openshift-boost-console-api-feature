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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.estafet.openshift.boost.commons.lib.env.ENV;
import com.estafet.openshift.boost.console.api.feature.dao.CommitDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.message.GitCommit;
import com.estafet.openshift.boost.console.api.feature.message.GitTag;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.RepoCommit;
import com.estafet.openshift.boost.console.api.feature.model.Version;

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
	public List<RepoCommit> getLastestRepoCommits(String repoId) {
		Repo repo = repoDAO.getRepo(repoId);
		List<GitCommit> commits = getRepoCommits(repoId, repo.getLastDate());
		repo.setLastDate(commits.get(0).getCommit().getCommitter().getDate());
		repoDAO.updateRepo(repo);
		return createRepoCommits(repo, commits);
	}
	
	private List<RepoCommit> createRepoCommits(Repo repo, List<GitCommit> commits) {
		if (!commits.isEmpty()) {
			List<RepoCommit> repoCommits = getRepoCommits(repo, commits);
			GitTag[] gitTags = getGitTags(repo.getName());
			Map<String, String> tags = commitTagMap(gitTags);
			String tag = gitTags.length > 0 ? nextVersion(gitTags) : "0.0.0"; 
			for (RepoCommit repoCommit : repoCommits) {
				String nextTag = tags.get(repoCommit.getSha());	
				if (nextTag != null) {
					repoCommit.setTag(tag);
					tag = nextTag;
				} else {
					repoCommit.setTag(tag);
				}
				if (commitDAO.getCommit(repo.getName(), repoCommit.getSha()) == null) {
					commitDAO.createRepoCommit(repoCommit);	
				}
			}
			return repoCommits;
		} else {
			return new ArrayList<RepoCommit>();
		}
	}

	private String nextVersion(GitTag[] gitTags) {
		return new Version(gitTags[0].getName()).increment().toString();
	}

	private List<RepoCommit> getRepoCommits(Repo repo, List<GitCommit> commits) {
		List<RepoCommit> repoCommits = new ArrayList<RepoCommit>(commits.size());
		for (GitCommit commit : commits) {
			repoCommits.add(commit.getRepoCommit(repo));
		}
		return repoCommits;
	}
	
	private List<GitCommit> getRepoCommits(String repo, String since) {
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
	
	private Map<String, String> commitTagMap(GitTag[] gitTags) {
		Map<String, String> commitTagMap = new HashMap<String, String>();
		for (GitTag gitTag : gitTags) {
			commitTagMap.put(gitTag.getCommit().getSha(), gitTag.getName());
		}
		return commitTagMap; 
	}

	private GitTag[] getGitTags(String repo) {
		String url = "https://api.github.com/repos/" + ENV.GITHUB + "/" + repo + "/tags";
		log.info(url);
		return restTemplate.getForObject(url, GitTag[].class);
	}

}
