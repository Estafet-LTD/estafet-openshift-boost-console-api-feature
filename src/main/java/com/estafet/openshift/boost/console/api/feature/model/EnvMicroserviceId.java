package com.estafet.openshift.boost.console.api.feature.model;

import java.io.Serializable;

public class EnvMicroserviceId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Env env;

	private String microservice;

	public EnvMicroserviceId(Env env, String microservice) {
		this.env = env;
		this.microservice = microservice;
	}

	public Env getEnv() {
		return env;
	}

	public String getMicroservice() {
		return microservice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((env == null) ? 0 : env.hashCode());
		result = prime * result + ((microservice == null) ? 0 : microservice.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnvMicroserviceId other = (EnvMicroserviceId) obj;
		if (env == null) {
			if (other.env != null)
				return false;
		} else if (!env.equals(other.env))
			return false;
		if (microservice == null) {
			if (other.microservice != null)
				return false;
		} else if (!microservice.equals(other.microservice))
			return false;
		return true;
	}

}
