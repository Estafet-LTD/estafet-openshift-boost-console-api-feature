package com.estafet.openshift.boost.console.api.feature.model;

import java.util.List;

public class VersionCommits {

	private final List<GitCommit> commits;
	
	public VersionCommits(List<GitCommit> commits) {
		this.commits = commits;
	}

	public boolean isIn(String commitId) {
		for (GitCommit commit : commits) {
			if (commit.getSha().equals(commitId)) {
				return true;
			}
		}
		return false;
	}
	
}
