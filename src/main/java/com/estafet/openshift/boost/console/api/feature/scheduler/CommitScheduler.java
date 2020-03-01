package com.estafet.openshift.boost.console.api.feature.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.estafet.openshift.boost.console.api.feature.service.RepoService;

@Component
public class CommitScheduler {

	@Autowired
	private RepoService repoService;

	@Scheduled(fixedRate = 60000)
	public void execute() {
		repoService.processRepos();
	}

}
