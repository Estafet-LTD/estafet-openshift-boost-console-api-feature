package com.estafet.openshift.boost.console.api.feature.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.estafet.openshift.boost.messages.features.CommitMessage;

@Entity
@Table(name = "REPO_COMMIT", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"REPO_ID", "SHA"}, name = "REPO_COMMIT_KEY") })
public class RepoCommit {

	@Id
	@SequenceGenerator(name = "REPO_COMMIT_ID_SEQ", sequenceName = "REPO_COMMIT_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPO_COMMIT_ID_SEQ")
	@Column(name = "REPO_COMMIT_ID")
	private Long id;

	@Column(name = "SHA", nullable = false)
	private String sha;
	
	@Column(name = "COMMITTED_DATE", nullable = false)
	private String commitedDate;
	
	@Column(name = "TAG", nullable = false)
	private String tag;

	@Column(name = "MESSAGE", nullable = false)
	private String message;
	
	@ManyToOne
	@JoinColumn(name = "FEATURE_ID", nullable = true, referencedColumnName = "FEATURE_ID", foreignKey = @ForeignKey(name = "COMMIT_TO_FEATURE_FK"))
	private Feature feature;
		
	@ManyToOne
	@JoinColumn(name = "REPO_ID", nullable = false, referencedColumnName = "REPO_ID", foreignKey = @ForeignKey(name = "COMMIT_TO_REPO_FK"))
	private Repo repo;

	public String getCommitedDate() {
		return commitedDate;
	}

	public void setCommitedDate(String commitedDate) {
		this.commitedDate = commitedDate;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public Repo getRepo() {
		return repo;
	}

	public void setRepo(Repo repo) {
		this.repo = repo;
	}
	
	public Date getDate() {
		return DateUtils.getDate(commitedDate);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((repo == null) ? 0 : repo.hashCode());
		result = prime * result + ((sha == null) ? 0 : sha.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepoCommit other = (RepoCommit) obj;
		if (repo == null) {
			if (other.repo != null)
				return false;
		} else if (!repo.equals(other.repo))
			return false;
		if (sha == null) {
			if (other.sha != null)
				return false;
		} else if (!sha.equals(other.sha))
			return false;
		return true;
	}
	
	public CommitMessage getCommitMessage() {
		return CommitMessage.builder()
				.setCommitId(getSha())
				.setMessage(getMessage())
				.setRepo(getRepo().getName())
				.build();
	}
	
	public static RepoCommitBuilder builder() {
		return new RepoCommitBuilder();
	}
	
	public static class RepoCommitBuilder {

		private String sha;
		private Repo repo;
		private String message;
		private String commitedDate;
		
		private RepoCommitBuilder() { }
		
		public RepoCommitBuilder setCommitedDate(String commitedDate) {
			this.commitedDate = commitedDate;
			return this;
		}

		public RepoCommitBuilder setMessage(String message) {
			this.message = message;
			return this;
		}

		public RepoCommitBuilder setRepo(Repo repo) {
			this.repo = repo;
			return this;
		}

		public RepoCommitBuilder setSha(String sha) {
			this.sha = sha;
			return this;
		}
		
		public RepoCommit build() {
			RepoCommit commit = new RepoCommit();
			repo.addCommit(commit);
			commit.setSha(sha);
			commit.setMessage(message);
			commit.setCommitedDate(commitedDate);
			return commit;
		}
		
	}

	@Override
	public String toString() {
		return "RepoCommit [id=" + id + ", sha=" + sha + ", commitedDate=" + commitedDate + ", tag=" + tag
				+ ", message=" + message + ", repo=" + repo.getName() + "]";
	}	
	
	

}
