package com.estafet.openshift.boost.console.api.feature.model;

public class RepoBuilder {

	private String name;

	private String microservice;

	public RepoBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public RepoBuilder setMicroservice(String microservice) {
		this.microservice = microservice;
		return this;
	}

	public Repo build() {
		Repo repo = new Repo();
		repo.setName(name);
		repo.setMicroservice(microservice);
		return repo;
	}
	
}
