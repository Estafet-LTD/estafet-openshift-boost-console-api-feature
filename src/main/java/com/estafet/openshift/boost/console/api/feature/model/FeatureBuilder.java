package com.estafet.openshift.boost.console.api.feature.model;

public class FeatureBuilder {

	private String featureId;
	
	private String title;
	
	private String description;

	private String status;

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
	
	public Feature build() {
		Feature feature = new Feature();
		feature.setFeatureId(featureId);
		if (description.length() > 255) {
			feature.setDescription(description.substring(0, 255));	
		} else {
			feature.setDescription(description);	
		}
		feature.setStatus(status);
		feature.setTitle(title);
		return feature;
	}
	
}
