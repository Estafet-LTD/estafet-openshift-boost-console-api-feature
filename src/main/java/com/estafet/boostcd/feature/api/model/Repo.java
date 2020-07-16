package com.estafet.boostcd.feature.api.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.estafet.boostcd.commons.git.Git;

@Entity
@Table(name = "REPO", uniqueConstraints = {
		@UniqueConstraint(columnNames = "MICROSERVICE", name = "MICROSERVICE_KEY"), 
		@UniqueConstraint(columnNames = "URL", name = "URL_KEY") })
public class Repo {

	@Id
	@Column(name = "REPO_ID", nullable = false)
	private String name;

	@Column(name = "URL", nullable = false)
	private String url;

	@Column(name = "MICROSERVICE", nullable = false)
	private String microservice;

	@Column(name = "LAST_DATE", nullable = true)
	private String lastDate;
	
	@OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<RepoCommit> commits = new HashSet<RepoCommit>();

	public void addCommit(RepoCommit repoCommit) {
		commits.add(repoCommit);
		repoCommit.setRepo(this);
	}

	public String getOrg() {
		return new Git(url).org();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastDate() {
		return lastDate;
	}

	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}

	public String getMicroservice() {
		return microservice;
	}

	public void setMicroservice(String microservice) {
		this.microservice = microservice;
	}

	public Set<RepoCommit> getCommits() {
		return commits;
	}

	public void setCommits(Set<RepoCommit> commits) {
		this.commits = commits;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Repo other = (Repo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public static RepoBuilder builder() {
		return new RepoBuilder();
	}
	
	public static class RepoBuilder {

		private String url;

		private String microservice;

		private RepoBuilder() {}

		public RepoBuilder setUrl(String url) {
			this.url = url;
			return this;
		}		

		public RepoBuilder setMicroservice(String microservice) {
			this.microservice = microservice;
			return this;
		}

		public Repo build() {
			Repo repo = new Repo();
			Git git = new Git(url);
			repo.setName(git.uri());
			repo.setUrl(url);
			repo.setMicroservice(microservice);
			return repo;
		}
		
	}

	@Override
	public String toString() {
		return "Repo [name=" + name + ", microservice=" + microservice + ", lastDate=" + lastDate + ", commits="
				+ commits + "]";
	}

	public boolean containsSha(String sha) {
		for (RepoCommit commit : commits) {
			if (commit.getSha().equals(sha)) {
				return true;
			}
		}
		return false;
	}

}
