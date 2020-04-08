package com.estafet.openshift.boost.console.api.feature.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("UNMATCHED")
public class Unmatched extends RepoCommit {

	public static UnmatchedBuilder builder() {
		return new UnmatchedBuilder();
	}
	
	public static class UnmatchedBuilder {

		private String sha;
		private Repo repo;
		private String message;
		
		private UnmatchedBuilder() { }

		public UnmatchedBuilder setMessage(String message) {
			this.message = message;
			return this;
		}

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
			unmatched.setMessage(message);
			repo.addCommit(unmatched);
			return unmatched;
		}
		
		
	}
	
}
