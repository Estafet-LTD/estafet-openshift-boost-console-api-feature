package com.estafet.openshift.boost.console.api.feature.model;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;

public class EnvBuilder {

	private String name;

	private Boolean live;

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
		env.setUpdatedDate(DateUtils.newDate());
		return env;
	}
	
}
