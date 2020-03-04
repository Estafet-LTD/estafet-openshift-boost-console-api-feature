package com.estafet.openshift.boost.console.api.feature.dto;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentDTO {

	private String name;

	private String updatedDate;

	private boolean live;
	
	private List<FeatureDTO> features = new ArrayList<FeatureDTO>();

	public void addFeature(FeatureDTO feature) {
		features.add(feature);
	}
	
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

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public List<FeatureDTO> getFeatures() {
		return features;
	}

	public void setFeatures(List<FeatureDTO> features) {
		this.features = features;
	}
	
	public static EnvironmentDTOBuilder builder() {
		return new EnvironmentDTOBuilder();
	}
	
}
