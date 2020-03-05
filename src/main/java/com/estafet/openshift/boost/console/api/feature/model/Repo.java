package com.estafet.openshift.boost.console.api.feature.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "REPO")
public class Repo {

	@Id
	@Column(name = "REPO_ID", nullable = false)
	private String name;

	@Column(name = "MICROSERVICE", nullable = false)
	private String microservice;

	@OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<RepoCommit> commits = new HashSet<RepoCommit>();

	public boolean contains(Feature feature) {
		for (RepoCommit commit : commits) {
			if (commit instanceof Matched && ((Matched) commit).getFeature().equals(feature)) {
				return true;
			}
		}
		return false;
	}

	public RepoCommit getCommit(String commitId) {
		for (RepoCommit commit : commits) {
			if (commit.getSha().equals(commitId)) {
				return commit;
			}
		}
		return null;
	}
	
	public boolean contains(RepoCommit repoCommit) {
		return commits.contains(repoCommit);
	}
	
	public void addCommit(RepoCommit repoCommit) {
		commits.add(repoCommit);
		repoCommit.setRepo(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}
