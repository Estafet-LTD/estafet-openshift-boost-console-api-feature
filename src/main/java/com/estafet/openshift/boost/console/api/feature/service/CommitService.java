package com.estafet.openshift.boost.console.api.feature.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.Unmatched;
import com.estafet.openshift.boost.console.api.feature.util.EnvUtil;
import com.estafet.openshift.boost.messages.model.UnmatchedCommitMessage;

@Service
public class CommitService {

	@Autowired
	private RepoDAO repoDAO;
	
	@Autowired
	private GithubService githubService; 
	
	@Transactional
	public void processUnmatched(UnmatchedCommitMessage message) {
		Repo repo = repoDAO.getRepo(message.getRepo());
		Unmatched unmatched = createUnmatched(message, repo);
		if (!repo.contains(unmatched)) {
			repo.addCommit(unmatched);
			repoDAO.update(repo);
		}
	}

	private Unmatched createUnmatched(UnmatchedCommitMessage message, Repo repo) {
		return Unmatched.builder()
						.setRepo(repo)
						.setSha(message.getCommitId())
						.setVersion(getVersion(message))
						.build();
	}

	private String getVersion(UnmatchedCommitMessage message) {
		return githubService.getVersionForCommit(EnvUtil.getGithub(), message.getRepo(), message.getCommitId());
	}
	
}
