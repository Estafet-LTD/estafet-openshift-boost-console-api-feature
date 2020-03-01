package com.estafet.openshift.boost.console.api.feature.variables;

public final class EnvVars {

	private EnvVars( ) {}
		
	public static final String getGithub() {
		return System.getenv("GITHUB");
	}

}
