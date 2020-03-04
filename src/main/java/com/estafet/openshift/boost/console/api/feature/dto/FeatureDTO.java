package com.estafet.openshift.boost.console.api.feature.dto;

public class FeatureDTO {

	private String featureId;

	private String title;

	private String description;

	private String status;

	private boolean promoted;

	private String unpromotedSince;

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isPromoted() {
		return promoted;
	}

	public void setPromoted(boolean promoted) {
		this.promoted = promoted;
	}

	public String getUnpromotedSince() {
		return unpromotedSince;
	}

	public void setUnpromotedSince(String unpromotedSince) {
		this.unpromotedSince = unpromotedSince;
	}
	
	public static FeatureDTOBuilder builder() {
		return new FeatureDTOBuilder();
	}

}
