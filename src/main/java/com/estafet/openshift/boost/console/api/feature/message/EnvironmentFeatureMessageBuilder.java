package com.estafet.openshift.boost.console.api.feature.message;

public class EnvironmentFeatureMessageBuilder {

	private String environment;

	private String featureId;

	private String title;

	private String description;

	private String status;

	private String deployedDate;

	private String migratedDate;

	public EnvironmentFeatureMessageBuilder setEnvironment(String environment) {
		this.environment = environment;
		return this;
	}

	public EnvironmentFeatureMessageBuilder setFeatureId(String featureId) {
		this.featureId = featureId;
		return this;
	}

	public EnvironmentFeatureMessageBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public EnvironmentFeatureMessageBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public EnvironmentFeatureMessageBuilder setStatus(String status) {
		this.status = status;
		return this;
	}

	public EnvironmentFeatureMessageBuilder setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
		return this;
	}

	public EnvironmentFeatureMessageBuilder setMigratedDate(String migratedDate) {
		this.migratedDate = migratedDate;
		return this;
	}
	
	public EnvFeatureMessage build() {
		EnvFeatureMessage envFeatureMessage = new EnvFeatureMessage();
		envFeatureMessage.setDeployedDate(deployedDate);
		envFeatureMessage.setDescription(description);
		envFeatureMessage.setEnvironment(environment);
		envFeatureMessage.setFeatureId(featureId);
		envFeatureMessage.setMigratedDate(migratedDate);
		envFeatureMessage.setStatus(status);
		envFeatureMessage.setTitle(title);
		return envFeatureMessage;
	}
}
