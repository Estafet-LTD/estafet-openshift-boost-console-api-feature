package com.estafet.openshift.boost.console.api.feature.model;

public enum PromoteStatus {
	
	NOT_PROMOTED("NOT_PROMOTED"),
	PARTIALLY_PROMOTED("PARTIALLY_PROMOTED"),
	FULLY_PROMOTED("FULLY_PROMOTED");

	private String value;

	PromoteStatus(String value){
		this.value=value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}