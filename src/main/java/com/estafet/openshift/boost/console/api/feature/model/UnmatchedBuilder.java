package com.estafet.openshift.boost.console.api.feature.model;

public class UnmatchedBuilder {

	private String sha;

	private Repo repo;

	public UnmatchedBuilder setSha(String sha) {
		this.sha = sha;
		return this;
	}

	public UnmatchedBuilder setRepo(Repo repo) {
		this.repo = repo;
		return this;
	}
	
	public Unmatched build() {
		Unmatched unmatched = new Unmatched();
		unmatched.setSha(sha);
		repo.addCommit(unmatched);
		return unmatched;
	}
	
	
}
