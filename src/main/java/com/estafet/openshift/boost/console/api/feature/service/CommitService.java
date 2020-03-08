package com.estafet.openshift.boost.console.api.feature.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.estafet.openshift.boost.console.api.feature.dao.CommitDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.model.Repo;
import com.estafet.openshift.boost.console.api.feature.model.Unmatched;
import com.estafet.openshift.boost.messages.model.UnmatchedCommitMessage;

@Service
public class CommitService {

	@Autowired
	private RepoDAO repoDAO;
	
	@Autowired
	private CommitDAO commitDAO;
	
	@Transactional
	public void processUnmatched(UnmatchedCommitMessage message) {
		
		if (commitDAO.getCommit(message.getRepo(), message.getCommitId()) == null) {
			Repo repo = repoDAO.getRepo(message.getRepo());
			repo.addCommit(createUnmatched(message, repo));
			repoDAO.updateRepo(repo);
		}
	}

	private Unmatched createUnmatched(UnmatchedCommitMessage message, Repo repo) {
		return Unmatched.builder()
						.setRepo(repo)
						.setSha(message.getCommitId())
						.build();
	}
	
}
