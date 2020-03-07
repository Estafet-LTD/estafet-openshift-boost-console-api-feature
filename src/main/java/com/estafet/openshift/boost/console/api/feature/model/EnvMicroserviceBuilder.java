package com.estafet.openshift.boost.console.api.feature.model;

public class EnvMicroserviceBuilder {

	private String version;

	private String deployedDate;
	
	private String microservice;

	public EnvMicroserviceBuilder setMicroservice(String microservice) {
		this.microservice = microservice;
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
		envMicroservice.setMicroservice(microservice);
		envMicroservice.setDeployedDate(deployedDate);
		envMicroservice.setVersion(version);
		return envMicroservice;
	}

}
