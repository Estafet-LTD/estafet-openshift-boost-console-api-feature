package com.estafet.openshift.boost.console.api.feature.model;

import java.util.List;

public class ProdEnv {

	private String name;

	private List<ProdApp> prodApps;

	private boolean tested;

	private boolean live;

	private String updatedDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ProdApp> getProdApps() {
		return prodApps;
	}

	public void setProdApps(List<ProdApp> prodApps) {
		this.prodApps = prodApps;
	}

	public boolean isTested() {
		return tested;
	}

	public void setTested(boolean tested) {
		this.tested = tested;
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

}
