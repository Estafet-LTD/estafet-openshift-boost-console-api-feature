package com.estafet.openshift.boost.console.api.feature.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.estafet.openshift.boost.messages.features.CommitMessage;

@Entity
@DiscriminatorValue("MATCHED")
public class Matched extends RepoCommit {

	@ManyToOne
	@JoinColumn(name = "FEATURE_ID", nullable = true, referencedColumnName = "FEATURE_ID", foreignKey = @ForeignKey(name = "COMMIT_TO_FEATURE_FK"))
	private Feature feature;
	
	@Column(name = "VERSION", nullable = true)
	private String version;
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	
	public CommitMessage getCommitMessage() {
		return CommitMessage.builder()
				.setCommitId(getSha())
				.setMessage(getMessage())
				.setRepo(getRepo().getName())
				.build();
	}
	
	public static MatchedBuilder builder() {
		return new MatchedBuilder();
	}
	
	public static class MatchedBuilder {

		private Feature feature;
		private String sha;
		private String version;
		private Repo repo;
		private String message;
		
		public MatchedBuilder() { }
		
		public MatchedBuilder setMessage(String message) {
			this.message = message;
			return this;
		}

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
			matched.setMessage(message);
			return matched;
		}
		
	}
	
}
