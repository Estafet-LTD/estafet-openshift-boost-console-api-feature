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
import com.estafet.openshift.boost.console.api.feature.model.CommitDate;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
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
	public List<GitCommit> getLastestRepoCommits(String repoId) {
		Repo repo = repoDAO.getRepo(repoId);
		List<GitCommit> commits = getRepoCommits(repoId, repo.getLastDate());
		repo.setLastDate(commits.get(0).getCommit().getCommitter().getDate());
		repoDAO.updateRepo(repo);
		updateCommitDates(repo, commits);
		return commits;
	}
	
	private void updateCommitDates(Repo repo, List<GitCommit> commits) {
		if (!commits.isEmpty()) {
			List<CommitDate> dates = getCommitDates(repo, commits);
			GitTag[] gitTags = getGitTags(repo.getName());
			Map<String, String> tags = commitTagMap(gitTags);
			String tag = gitTags.length > 0 ? nextVersion(gitTags) : "0.0.0"; 
			for (CommitDate date : dates) {
				String nextTag = tags.get(date.getSha());	
				if (nextTag != null) {
					date.setTag(tag);
					tag = nextTag;
				} else {
					date.setTag(tag);
				}
				commitDAO.createCommitDate(date);
			}
		}
	}

	private String nextVersion(GitTag[] gitTags) {
		return new Version(gitTags[0].getName()).increment().toString();
	}

	private List<CommitDate> getCommitDates(Repo repo, List<GitCommit> commits) {
		List<CommitDate> dates = new ArrayList<CommitDate>(commits.size());
		for (GitCommit commit : commits) {
			dates.add(commit.getCommitDate(repo));
		}
		return dates;
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
