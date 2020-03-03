package com.estafet.openshift.boost.console.api.feature.variables;

public final class EnvVars {

	private EnvVars( ) {}
		
	public static final String getGithub() {
		return System.getenv("GITHUB");
	}
	
	public static final String getProduct() {
		return System.getenv("PRODUCT");
	}
	
	public static final String getOpenshift() {
		return System.getenv("OPENSHIFT_HOST_PORT");
	}
	
	public static final String getOpenshiftUser() {
		return System.getenv("OPENSHIFT_USER");
	}
	
	public static final String getOpenshiftPwd() {
		return System.getenv("OPENSHIFT_PASSWORD");
	}

}
