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

	@Column(name = "URL", nullable = true)
	private String url;

	@OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Matched> matched = new HashSet<Matched>();

	@OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EnvFeature> envFeatures = new HashSet<EnvFeature>();

	public void addMatched(Matched matched) {
		this.matched.add(matched);
		matched.setFeature(this);
	}

	public void addEnvFeature(EnvFeature envFeature) {
		this.envFeatures.add(envFeature);
		envFeature.setFeature(this);
	}

	public Set<Matched> getMatched() {
		return matched;
	}

	public void setMatched(Set<Matched> matched) {
		this.matched = matched;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Set<EnvFeature> getEnvFeatures() {
		return envFeatures;
	}

	public void setEnvFeatures(Set<EnvFeature> envFeatures) {
		this.envFeatures = envFeatures;
	}

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
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

	@Override
	public String toString() {
		return "Feature [featureId=" + featureId + ", title=" + title + ", description=" + description + ", status="
				+ status + "]";
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
		this.status = recent.status;
	}

	public static FeatureBuilder builder() {
		return new FeatureBuilder();
	}

}
