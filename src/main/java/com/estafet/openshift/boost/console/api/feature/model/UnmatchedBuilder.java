package com.estafet.openshift.boost.console.api.feature.model;

public class UnmatchedBuilder {

	private String sha;
	
	private String version;

	private Repo repo;

	public UnmatchedBuilder setSha(String sha) {
		this.sha = sha;
		return this;
	}

	public UnmatchedBuilder setVersion(String version) {
		this.version = version;
		return this;
	}

	public UnmatchedBuilder setRepo(Repo repo) {
		this.repo = repo;
		return this;
	}
	
	public Unmatched build() {
		Unmatched unmatched = new Unmatched();
		unmatched.setSha(sha);
		unmatched.setVersion(version);
		repo.addCommit(unmatched);
		return unmatched;
	}
	
	
}
