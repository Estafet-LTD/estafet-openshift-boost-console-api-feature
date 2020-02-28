package com.estafet.openshift.boost.console.api.feature.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ENV")
public class Env {

	@Id
	@Column(name = "ENV_ID", nullable = false)
	private String name;
	
	@Column(name = "REPO", nullable = false)
	private String repo;

	@Column(name = "UPDATED_DATE", nullable = false)
	private String updatedDate;

	@OneToMany(mappedBy = "appEnv", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Feature> features = new ArrayList<Feature>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setBuildApps(List<Feature> features) {
		this.features = features;
	}

	public boolean update(Env env) {
		if (changed(env)) {
			for (Feature recentFeature : env.getFeatures()) {
				Feature app = getFeature(recentFeature.getFeatureId());
				if (app == null) {
					addFeature(recentFeature);
				} else if (app.getFeatureId().equals(recentFeature.getFeatureId())) {
					app.update(recentFeature);
				}
			}
			return true;	
		}
		return false;
	}

	public Env addFeature(Feature app) {
		app.setAppEnv(this);
		features.add(app);
		return this;
	}
	
	private Feature getFeature(String featureId) {
		for (Feature feature : features) {
			if (feature.getFeatureId().equals(featureId)) {
				return feature;
			}
		}
		return null;
	}

	private boolean changed(Env env) {
		for (Feature recentFeature : env.getFeatures()) {
			Feature app = getFeature(recentFeature.getFeatureId());
			if (app == null || !app.isEqualTo(recentFeature)) {
				return true;
			}
		}
		return false;
	}

}
