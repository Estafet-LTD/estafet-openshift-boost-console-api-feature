package com.estafet.openshift.boost.console.api.feature.model;

public class EnvMicroserviceBuilder {

	private String microservice;

	private String version;

	private String deployedDate;

	private Repo repo;

	public EnvMicroserviceBuilder setMicroservice(String microservice) {
		this.microservice = microservice;
		return this;
	}

	public EnvMicroserviceBuilder setRepo(Repo repo) {
		this.repo = repo;
		return this;
	}

	public EnvMicroserviceBuilder setVersion(String version) {
		this.version = version;
		return this;
	}

	public EnvMicroserviceBuilder setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
		return this;
	}

	public EnvMicroservice build() {
		EnvMicroservice envMicroservice = new EnvMicroservice();
		envMicroservice.setDeployedDate(deployedDate);
		envMicroservice.setMicroservice(microservice);
		envMicroservice.setVersion(version);
		repo.addEnvMicroservice(envMicroservice);
		return envMicroservice;
	}

}
