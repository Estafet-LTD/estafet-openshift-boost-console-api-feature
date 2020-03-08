package com.estafet.openshift.boost.console.api.feature.dto;

import java.util.Collections;
import java.util.Set;

import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;

public class EnvironmentDTOBuilder {

	private String name;

	private String updatedDate;

	private Boolean live;
	
	private Set<EnvFeature> envFeatures;

	public EnvironmentDTOBuilder setEnvFeatures(Set<EnvFeature> envFeatures) {
		this.envFeatures = envFeatures;
		return this;
	}

	public EnvironmentDTOBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public EnvironmentDTOBuilder setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
		return this;
	}

	public EnvironmentDTOBuilder setLive(boolean live) {
		this.live = live;
		return this;
	}
	
	public EnvironmentDTO build() {
		EnvironmentDTO dto = new EnvironmentDTO();
		dto.setLive(live);
		dto.setName(name);
		dto.setUpdatedDate(updatedDate);
		for (EnvFeature envFeature : envFeatures) {
			dto.addFeature(envFeature.getFeatureDTO());
		}
		Collections.sort(dto.getFeatures(), new FeatureDTOComparator());
		return dto;
	}
	
	
	
}
