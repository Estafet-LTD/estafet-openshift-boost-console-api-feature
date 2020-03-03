package com.estafet.openshift.boost.console.api.feature.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "FEATURE")
public class Feature {

	@Id
	@Column(name = "FEATURE_ID", nullable = false)
	private String featureId;

	@Column(name = "TITLE", nullable = true)
	private String title;

	@Column(name = "DESCRIPTION", nullable = true)
	private String description;

	@Column(name = "STATUS", nullable = true)
	private String status;

	@Column(name = "UPDATED_DATE", nullable = false)
	private String updatedDate;

	@Column(name = "DEPLOYED_DATE", nullable = true)
	private String deployedDate;

	@Column(name = "PROMOTED", nullable = false)
	private boolean promoted = false;

	@JsonIgnore
	@OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<RepoCommit> commits = new HashSet<RepoCommit>();
	
	@JsonIgnore
	@ManyToMany(mappedBy = "envs")
	private List<Env> envs = new ArrayList<Env>();

	@JsonIgnore
	@ManyToMany(mappedBy = "features")
	private List<Repo> repos = new ArrayList<Repo>();

	public List<Repo> getRepos() {
		return repos;
	}

	public void setRepos(List<Repo> repos) {
		this.repos = repos;
	}

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public String getDeployedDate() {
		return deployedDate;
	}

	public void setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
	}

	public boolean isPromoted() {
		return promoted;
	}

	public void setPromoted(boolean promoted) {
		this.promoted = promoted;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public List<Env> getEnvs() {
		return envs;
	}

	public void setEnvs(List<Env> envs) {
		this.envs = envs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((featureId == null) ? 0 : featureId.hashCode());
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
		Feature other = (Feature) obj;
		if (featureId == null) {
			if (other.featureId != null)
				return false;
		} else if (!featureId.equals(other.featureId))
			return false;
		return true;
	}

	public void update(Feature recent) {
		this.updatedDate = recent.updatedDate;
		this.status = recent.status;
		try {
			if (DateUtils.dateFormat.parse(this.deployedDate).after(DateUtils.dateFormat.parse(recent.deployedDate))) {
				this.deployedDate = recent.deployedDate;
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static FeatureBuilder builder() {
		return new FeatureBuilder();
	}

}
