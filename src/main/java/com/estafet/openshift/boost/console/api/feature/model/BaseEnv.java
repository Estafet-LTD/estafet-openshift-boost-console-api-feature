package com.estafet.openshift.boost.console.api.feature.model;

import java.util.List;

public abstract class BaseEnv {

	private String name;
	private String updatedDate;
		
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	public abstract List<BaseApp> getApps();

}