package com.estafet.openshift.boost.console.api.feature.message;

public class BuildApp extends BaseApp {

	private boolean canRelease;

	private String errors;

	private String updatedDate;
	
	private boolean deployed;

	public boolean isDeployed() {
		return deployed;
	}

	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}

	public boolean isCanRelease() {
		return canRelease;
	}

	public void setCanRelease(boolean canRelease) {
		this.canRelease = canRelease;
	}

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

}
