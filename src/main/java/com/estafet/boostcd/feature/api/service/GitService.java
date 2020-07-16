package com.estafet.boostcd.feature.api.service;

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

import com.estafet.boostcd.feature.api.dao.CommitDAO;
import com.estafet.boostcd.feature.api.dao.RepoDAO;
import com.estafet.boostcd.feature.api.message.GitCommit;
import com.estafet.boostcd.feature.api.message.GitTag;
import com.estafet.boostcd.feature.api.model.Repo;
import com.estafet.boostcd.feature.api.model.RepoCommit;
import com.estafet.boostcd.feature.api.model.Version;

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
		List<GitCommit> commits = getRepoCommits(repo);
		repo.setLastDate(commits.get(0).getCommit().getCommitter().getDate());
		repoDAO.updateRepo(repo);
		return createRepoCommits(repo, commits);
	}
	
	private List<RepoCommit> createRepoCommits(Repo repo, List<GitCommit> commits) {
		List<RepoCommit> result = new ArrayList<RepoCommit>();
		if (!commits.isEmpty()) {
			List<RepoCommit> repoCommits = getRepoCommits(commits);
			GitTag[] gitTags = getGitTags(repo);
			Map<String, String> tags = commitTagMap(gitTags);
			String tag = gitTags.length > 0 ? nextVersion(gitTags) : "0.0.0"; 
			for (RepoCommit repoCommit : repoCommits) {
				if (!repo.containsSha(repoCommit.getSha())) {
					String nextTag = tags.get(repoCommit.getSha());	
					if (nextTag != null) {
						repoCommit.setTag(tag);
						tag = nextTag;
					} else {
						repoCommit.setTag(tag);
					}
					result.add(saveCommit(repo, repoCommit));	
				}
			}
		}
		return result;
	}

	private RepoCommit saveCommit(Repo repo, RepoCommit repoCommit) {
		repo.addCommit(repoCommit);
		commitDAO.createRepoCommit(repoCommit);
		return repoCommit;
	}

	private String nextVersion(GitTag[] gitTags) {
		return new Version(gitTags[0].getName()).increment().toString();
	}

	private List<RepoCommit> getRepoCommits(List<GitCommit> commits) {
		List<RepoCommit> repoCommits = new ArrayList<RepoCommit>(commits.size());
		for (GitCommit commit : commits) {
			repoCommits.add(commit.getRepoCommit());
		}
		return repoCommits;
	}
	
	private List<GitCommit> getRepoCommits(Repo repo) {
		List<GitCommit> commits = new ArrayList<GitCommit>();
		List<GitCommit> pageCommits = new ArrayList<GitCommit>();
		int page = 1;
		do {
			pageCommits = getRepoCommitsByPage(repo, page);
			commits.addAll(pageCommits);
			page++;
		} while (!pageCommits.isEmpty());
		return commits;
	}
	
	private List<GitCommit> getRepoCommitsByPage(Repo repo, int page) {
		String url = "https://api.github.com/repos/" + repo.getOrg() + "/" + repo.getName() + "/commits?page=" + page;
		if (repo.getLastDate() != null) {
			url += "&since=" + repo.getLastDate(); 
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

	private GitTag[] getGitTags(Repo repo) {
		String url = "https://api.github.com/repos/" + repo.getOrg() + "/" + repo.getName() + "/tags";
		log.info(url);
		return restTemplate.getForObject(url, GitTag[].class);
	}

}
