package com.estafet.openshift.boost.console.api.feature.util;

public final class EnvUtil {

	private EnvUtil( ) {}
		
	public static final String getGithub() {
		return System.getenv("GITHUB");
	}
	
	public static final String getGithubUser() {
		return System.getenv("GITHUB_USER");
	}
	
	public static final String getGithubPwd() {
		return System.getenv("GITHUB_PASSWORD");
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
