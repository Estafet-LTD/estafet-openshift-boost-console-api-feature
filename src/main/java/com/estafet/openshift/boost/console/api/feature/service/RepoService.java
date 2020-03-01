package com.estafet.openshift.boost.console.api.feature.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.CommitDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.jms.CommitProducer;
import com.estafet.openshift.boost.console.api.feature.model.BuildEnv;
import com.estafet.openshift.boost.console.api.feature.model.GitCommit;
import com.estafet.openshift.boost.console.api.feature.model.Repo;

@Service
public class RepoService {

	@Autowired
	private GithubService githubService;
	
	@Autowired
	private FeatureService featureService;

	@Autowired
	private RepoDAO repoDAO;

	@Autowired
	private CommitDAO commitDAO;
	
	@Autowired
	private CommitProducer commitProducer;

	@Transactional
	public void updateRepo(BuildEnv buildEnv) {

	}

	@Transactional(readOnly = true)
	public void processRepos() {
		for (Repo repo : repoDAO.getRepos()) {
			for (GitCommit commit : githubService.getRepoCommits(repo.getName())) {
				if (isInCompleteFeature(repo, commit) || isUnmatched(repo, commit)) {
					commitProducer.sendMessage(commit.createCommitMessage(repo.getName()));
				}
			}
		}
	}

	private boolean isInCompleteFeature(Repo repo, GitCommit commit) {
		return featureService.isIncompleteFeature(repo.getName(), commit.getSha());
	}

	private boolean isUnmatched(Repo repo, GitCommit commit) {
		return commitDAO.getUnmatched(repo.getName(), commit.getSha()) == null;
	}

}
