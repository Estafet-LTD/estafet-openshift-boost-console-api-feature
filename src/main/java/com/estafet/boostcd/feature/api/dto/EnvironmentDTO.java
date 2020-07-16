package com.estafet.boostcd.feature.api.dto;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentDTO {

	private String name;
	private String displayName;
	private String updatedDate;
	private Boolean live;
	private Boolean tested;
	
	private List<FeatureDTO> features = new ArrayList<FeatureDTO>();

	public void addFeature(FeatureDTO feature) {
		features.add(feature);
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Boolean getTested() {
		return tested;
	}

	public void setTested(Boolean tested) {
		this.tested = tested;
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

	public Boolean getLive() {
		return live;
	}

	public void setLive(Boolean live) {
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
	
	public static class EnvironmentDTOBuilder {

		private String name;
		private String updatedDate;
		private String displayName;
		private Boolean live;
		private Boolean tested;
		
		private EnvironmentDTOBuilder() { }

		public EnvironmentDTOBuilder setDisplayName(String displayName) {
			this.displayName = displayName;
			return this;
		}

		public EnvironmentDTOBuilder setTested(Boolean tested) {
			this.tested = tested;
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

		public EnvironmentDTOBuilder setLive(Boolean live) {
			this.live = live;
			return this;
		}
		
		public EnvironmentDTO build() {
			EnvironmentDTO dto = new EnvironmentDTO();
			dto.setLive(live);
			dto.setName(name);
			dto.setDisplayName(displayName);
			dto.setUpdatedDate(updatedDate);
			dto.setTested(tested);
			return dto;
		}
		
	}
	
}
