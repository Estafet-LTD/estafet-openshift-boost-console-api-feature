package com.estafet.openshift.boost.console.api.feature.message;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EnvFeatureMessage {

	private String environment;

	private String featureId;

	private String title;

	private String description;

	private String status;

	private String deployedDate;

	private String migratedDate;

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

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

	public String getDeployedDate() {
		return deployedDate;
	}

	public void setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
	}

	public String getMigratedDate() {
		return migratedDate;
	}

	public void setMigratedDate(String migratedDate) {
		this.migratedDate = migratedDate;
	}
	
	public static EnvironmentFeatureMessageBuilder builder() {
		return new EnvironmentFeatureMessageBuilder();
	}
	
	public static EnvFeatureMessage fromJSON(String message) {
		try {
			return new ObjectMapper().readValue(message, EnvFeatureMessage.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String toJSON() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
