package com.estafet.openshift.boost.console.api.feature.message;

import java.util.List;

public abstract class BaseEnv {

	private String name;

	private String updatedDate;

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public abstract List<BaseApp> getApps();

}