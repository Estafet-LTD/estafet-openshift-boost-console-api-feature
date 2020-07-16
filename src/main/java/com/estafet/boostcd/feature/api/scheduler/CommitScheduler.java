package com.estafet.boostcd.feature.api.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.estafet.boostcd.feature.api.dao.RepoDAO;
import com.estafet.boostcd.feature.api.jms.CommitProducer;
import com.estafet.boostcd.feature.api.model.Feature;
import com.estafet.boostcd.feature.api.model.Repo;
import com.estafet.boostcd.feature.api.model.RepoCommit;
import com.estafet.boostcd.feature.api.service.FeatureService;
import com.estafet.boostcd.feature.api.service.GitService;

@Component
public class CommitScheduler {

	@Autowired
	private RepoDAO repoDAO;
	
	@Autowired
	private FeatureService featureService;
	
	@Autowired
	private CommitProducer commitProducer;
		
	@Autowired
	private GitService gitService;
	
	@Scheduled(fixedRate = 60000)
	public void execute() {
		for (Repo repo : repoDAO.getRepos()) {
			for (RepoCommit commit : gitService.getLastestRepoCommits(repo.getName())) {
				commitProducer.sendMessage(commit.getCommitMessage());
			}
		}
		for (Feature feature : featureService.getIncompleteFeatures()) {
			for (RepoCommit matched : feature.getMatched()) {
				commitProducer.sendMessage(matched.getCommitMessage());
			}
		}
	}

}
