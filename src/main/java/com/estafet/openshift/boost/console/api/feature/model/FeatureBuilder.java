package com.estafet.openshift.boost.console.api.feature.model;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;

public class FeatureBuilder {

	private String featureId;
	
	private String title;
	
	private String description;

	private String status;

	private String deployedDate;

	private Boolean promoted;

	public FeatureBuilder setFeatureId(String featureId) {
		this.featureId = featureId;
		return this;
	}

	public FeatureBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public FeatureBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public FeatureBuilder setStatus(String status) {
		this.status = status;
		return this;
	}

	public FeatureBuilder setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
		return this;
	}

	public FeatureBuilder setPromoted(Boolean promoted) {
		this.promoted = promoted;
		return this;
	}
	
	public Feature build() {
		Feature feature = new Feature();
		feature.setFeatureId(featureId);
		feature.setDeployedDate(deployedDate);
		feature.setDescription(description);
		feature.setPromoted(promoted);
		feature.setStatus(status);
		feature.setTitle(title);
		feature.setUpdatedDate(DateUtils.newDate());
		return feature;
	}
	
}
