package com.estafet.openshift.boost.console.api.feature.model;

public class MicroserviceBuilder {

	private String name;
	
	private String version;

	private String deployedDate;
	
	private String repo;

	public MicroserviceBuilder setRepo(String repo) {
		this.repo = repo;
		return this;
	}

	public MicroserviceBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public MicroserviceBuilder setVersion(String version) {
		this.version = version;
		return this;
	}

	public MicroserviceBuilder setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
		return this;
	}
	
	public Microservice build() {
		Microservice microservice = new Microservice();
		microservice.setDeployedDate(deployedDate);
		microservice.setName(name);
		microservice.setVersion(version);
		microservice.setRepo(repo);
		return microservice;
	}
	
	
}
