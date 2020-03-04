package com.estafet.openshift.boost.console.api.feature.dto;

public class EnvironmentDTOBuilder {

	private String name;

	private String updatedDate;

	private Boolean live;

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
		return dto;
	}
	
	
	
}
