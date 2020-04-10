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

@Entity
@Table(name = "COMMIT_DATE", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"REPO_ID", "SHA"}, name = "COMMIT_DATE_KEY") })
public class CommitDate {

	@Id
	@SequenceGenerator(name = "COMMIT_DATE_ID_SEQ", sequenceName = "COMMIT_DATE_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMMIT_DATE_ID_SEQ")
	@Column(name = "COMMIT_DATE_ID")
	private Long id;
	
	@Column(name = "SHA", nullable = false)
	private String sha;
	
	@Column(name = "COMMITTED_DATE", nullable = false)
	private String commitedDate;
	
	@Column(name = "TAG", nullable = false)
	private String tag;
	
	@ManyToOne
	@JoinColumn(name = "REPO_ID", nullable = false, referencedColumnName = "REPO_ID", foreignKey = @ForeignKey(name = "COMMIT_DATE_TO_REPO_FK"))
	private Repo repo;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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

	public String getCommitedDate() {
		return commitedDate;
	}

	public void setCommitedDate(String commitedDate) {
		this.commitedDate = commitedDate;
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
		CommitDate other = (CommitDate) obj;
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
	
	public static CommitDateBuilder builder() {
		return new CommitDateBuilder();
	}
	
	public static class CommitDateBuilder {
		
		private String sha;
		private String commitedDate;
		private Repo repo;
		
		private CommitDateBuilder() {}
		
		public CommitDateBuilder setSha(String sha) {
			this.sha = sha;
			return this;
		}
		public CommitDateBuilder setCommitedDate(String commitedDate) {
			this.commitedDate = commitedDate;
			return this;
		}
		public CommitDateBuilder setRepo(Repo repo) {
			this.repo = repo;
			return this;
		}
		
		public CommitDate build() {
			CommitDate commitDate = new CommitDate();
			commitDate.setSha(sha);
			commitDate.setCommitedDate(commitedDate);
			repo.addDate(commitDate);
			return commitDate;
		}
				
	}

}
