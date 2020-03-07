package com.estafet.openshift.boost.console.api.feature.model;

public class EnvBuilder {

	private String name;

	private Boolean live;
	
	private String updatedDate;

	public EnvBuilder setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
		return this;
	}

	public EnvBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public EnvBuilder setLive(Boolean live) {
		this.live = live;
		return this;
	}
	
	public Env build() {
		Env env = new Env();
		env.setLive(live);
		env.setName(name);
		env.setUpdatedDate(updatedDate);
		return env;
	}
	
}
