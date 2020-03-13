package com.estafet.openshift.boost.console.api.feature.dto;

public class FeatureDTOBuilder {

	private String featureId;

	private String title;

	private String description;

	private String status;

	private boolean promoted;

	private String waitingSince;
	
	private String url;

	public FeatureDTOBuilder setUrl(String url) {
		this.url = url;
		return this;
	}

	public FeatureDTOBuilder setFeatureId(String featureId) {
		this.featureId = featureId;
		return this;
	}

	public FeatureDTOBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public FeatureDTOBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public FeatureDTOBuilder setStatus(String status) {
		this.status = status;
		return this;
	}

	public FeatureDTOBuilder setPromoted(boolean promoted) {
		this.promoted = promoted;
		return this;
	}

	public FeatureDTOBuilder setWaitingSince(String waitingSince) {
		this.waitingSince = waitingSince;
		return this;
	}
	
	public FeatureDTO build() {
		FeatureDTO dto = new FeatureDTO();
		dto.setUrl(url);
		dto.setDescription(description);
		dto.setFeatureId(featureId);
		dto.setPromoted(promoted);
		dto.setStatus(status);
		dto.setTitle(title);
		dto.setWaitingSince(waitingSince);
		return dto;
	}
	
}
