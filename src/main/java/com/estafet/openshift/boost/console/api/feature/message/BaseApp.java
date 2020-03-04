package com.estafet.openshift.boost.console.api.feature.message;

import java.util.Map;

import com.estafet.openshift.boost.console.api.feature.model.Microservice;

public class BaseApp {

	private String name;
	
	private String version;

	private String deployedDate;

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getVersion() {
		return version;
	}

	public final void setVersion(String version) {
		this.version = version;
	}

	public final String getDeployedDate() {
		return deployedDate;
	}

	public final void setDeployedDate(String deployedDate) {
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