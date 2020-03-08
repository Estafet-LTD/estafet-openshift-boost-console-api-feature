package com.estafet.openshift.boost.console.api.feature.model;

public class MatchedBuilder {

	private Feature feature;
	
	private String sha;
	
	private String version;
	
	private Repo repo;
	
	public MatchedBuilder setRepo(Repo repo) {
		this.repo = repo;
		return this;
	}

	public MatchedBuilder setFeature(Feature feature) {
		this.feature = feature;
		return this;
	}

	public MatchedBuilder setSha(String sha) {
		this.sha = sha;
		return this;
	}

	public MatchedBuilder setVersion(String version) {
		this.version = version;
		return this;
	}
	
	public Matched build() {
		Matched matched = new Matched();
		feature.addMatched(matched);
		repo.addCommit(matched);
		matched.setSha(sha);
		matched.setVersion(version);
		return matched;
	}
	
}
