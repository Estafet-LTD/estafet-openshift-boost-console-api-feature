package com.estafet.openshift.boost.console.api.feature.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.FeatureDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.jms.CommitProducer;
import com.estafet.openshift.boost.console.api.feature.model.Feature;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.RepoCommit;
import com.estafet.openshift.boost.console.api.feature.service.GitService;

@Component
public class CommitScheduler {

	@Autowired
	private RepoDAO repoDAO;
	
	@Autowired
	private FeatureDAO featureDAO;
	
	@Autowired
	private CommitProducer commitProducer;
		
	@Autowired
	private GitService gitService;
	
	@Transactional
	@Scheduled(fixedRate = 60000)
	public void execute() {
		for (Repo repo : repoDAO.getRepos()) {
			for (RepoCommit commit : gitService.getLastestRepoCommits(repo.getName())) {
				commitProducer.sendMessage(commit.getCommitMessage());
			}
		}
		for (Feature feature : featureDAO.getIncompleteFeatures()) {
			for (RepoCommit matched : feature.getMatched()) {
				commitProducer.sendMessage(matched.getCommitMessage());
			}
		}
	}

}
