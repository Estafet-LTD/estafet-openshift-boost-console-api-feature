package com.estafet.openshift.boost.console.api.feature.model;

import java.util.Map;

public class BaseApp {

	private String name;
	
	private String version;

	private String deployedDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDeployedDate() {
		return deployedDate;
	}

	public void setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
	}
		
	public Microservice createMicroservice(Map<String, String> microservicesMap) {
		Microservice microservice = new Microservice();
		microservice.setDeployedDate(deployedDate);
		microservice.setName(name);
		microservice.setVersion(version);
		microservice.setRepo(microservicesMap.get(name));
		return microservice;
	}

}