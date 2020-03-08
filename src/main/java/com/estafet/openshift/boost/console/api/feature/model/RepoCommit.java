package com.estafet.openshift.boost.console.api.feature.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "MATCHED_TYPE")
@Table(name = "REPO_COMMIT")
public abstract class RepoCommit {

	@Id
	@SequenceGenerator(name = "REPO_COMMIT_ID_SEQ", sequenceName = "REPO_COMMIT_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPO_COMMIT_ID_SEQ")
	@Column(name = "REPO_COMMIT_ID")
	private Long id;

	@Column(name = "SHA", nullable = false)
	private String sha;
	
	@ManyToOne
	@JoinColumn(name = "REPO_ID", nullable = false, referencedColumnName = "REPO_ID", foreignKey = @ForeignKey(name = "COMMIT_TO_REPO_FK"))
	private Repo repo;

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

}
