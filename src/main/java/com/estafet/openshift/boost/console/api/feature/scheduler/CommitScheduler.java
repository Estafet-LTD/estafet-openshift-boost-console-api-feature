package com.estafet.openshift.boost.console.api.feature.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.CommitDAO;
import com.estafet.openshift.boost.console.api.feature.dao.FeatureDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.jms.CommitProducer;
import com.estafet.openshift.boost.console.api.feature.message.GitCommit;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.service.GithubService;

@Component
public class CommitScheduler {

	@Autowired
	private RepoDAO repoDAO;
	
	@Autowired
	private CommitDAO commitDAO;
	
	@Autowired
	private CommitProducer commitProducer;
	
	@Autowired
	private FeatureDAO featureDAO;
		
	@Autowired
	private GithubService githubService;
	
	@Transactional(readOnly = true)
	@Scheduled(fixedRate = 60000)
	public void execute() {
		for (Repo repo : repoDAO.getRepos()) {
			for (GitCommit commit : githubService.getRepoCommits(repo.getName())) {
				if (isInCompleteFeature(repo, commit) || isUnmatched(repo, commit)) {
					commitProducer.sendMessage(commit.createCommitMessage(repo.getName()));
				}
			}
		}
	}

	private boolean isInCompleteFeature(Repo repo, GitCommit commit) {
		Feature feature = featureDAO.getFeatureByCommit(repo.getName(), commit.getSha());
		return feature == null || !feature.getStatus().equals("DONE");
	}

	private boolean isUnmatched(Repo repo, GitCommit commit) {
		return commitDAO.getUnmatched(repo.getName(), commit.getSha()) == null;
	}

}
