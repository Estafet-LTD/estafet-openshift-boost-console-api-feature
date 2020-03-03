package com.estafet.openshift.boost.console.api.feature.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(
		name = "REPO_FEATURE", 
		joinColumns = @JoinColumn(name = "REPO_ID", foreignKey = @ForeignKey(name = "REPO_FEATURE_REPO_ID_FK")), 
		inverseJoinColumns = @JoinColumn(name = "FEATURE_ID", foreignKey = @ForeignKey(name = "REPO_FEATURE_FEATURE_ID_FK"))
	)
	private Set<Feature> features = new HashSet<Feature>();
	
	@OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<RepoCommit> commits = new HashSet<RepoCommit>();
	
	public void addCommit(RepoCommit repoCommit) {
		commits.add(repoCommit);
		repoCommit.setRepo(this);
	}

	public void addFeature(Feature feature) {
		features.add(feature);
		feature.getRepos().add(this);
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

}
