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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "REPO", uniqueConstraints = {
		@UniqueConstraint(columnNames = "microservice", name = "MICROSERVICE_KEY") })
public class Repo {

	@Id
	@Column(name = "REPO_ID", nullable = false)
	private String name;

	@Column(name = "MICROSERVICE", nullable = false)
	private String microservice;

	@OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<RepoCommit> commits = new HashSet<RepoCommit>();

	public Set<Feature> getFeatures() {
		Set<Feature> features = new HashSet<Feature>();
		for (RepoCommit commit : commits) {
			if (commit instanceof Matched) {
				features.add(((Matched) commit).getFeature());
			}
		}
		return features;
	}

	public RepoCommit getCommit(String commitId) {
		for (RepoCommit commit : commits) {
			if (commit.getSha().equals(commitId)) {
				return commit;
			}
		}
		return null;
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

}
