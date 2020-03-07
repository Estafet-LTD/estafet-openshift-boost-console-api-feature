package com.estafet.openshift.boost.console.api.feature.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.jms.CommitProducer;
import com.estafet.openshift.boost.console.api.feature.message.GitCommit;
import com.estafet.openshift.boost.console.api.feature.model.Matched;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.RepoCommit;
import com.estafet.openshift.boost.console.api.feature.service.GithubService;
import com.estafet.openshift.boost.console.api.feature.util.EnvUtil;

@Component
public class CommitScheduler {

	@Autowired
	private RepoDAO repoDAO;
	
	@Autowired
	private CommitProducer commitProducer;
		
	@Autowired
	private GithubService githubService;
	
	@Transactional(readOnly = true)
	//@Scheduled(fixedRate = 300000)
	public void execute() {
		for (Repo repo : repoDAO.getRepos()) {
			for (GitCommit gitCommit : githubService.getRepoCommits(EnvUtil.getGithub(), repo.getName())) {
				RepoCommit commit = repo.getCommit(gitCommit.getSha());
				if (commit == null || (commit instanceof Matched && ((Matched)commit).getFeature().getStatus().equals("DONE"))) {
					commitProducer.sendMessage(gitCommit.createCommitMessage(repo.getName()));
				}
			}
		}
	}

}
