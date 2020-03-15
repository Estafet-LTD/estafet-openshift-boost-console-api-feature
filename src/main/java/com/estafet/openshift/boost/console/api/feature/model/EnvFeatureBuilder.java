package com.estafet.openshift.boost.console.api.feature.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.estafet.openshift.boost.messages.features.MissingFieldException;

public class EnvFeatureBuilder {

	private static final Logger log = LoggerFactory.getLogger(EnvFeatureBuilder.class);
	
	private String deployedDate;

	private String migratedDate;

	private Feature feature;
	
	private Env env;

	public EnvFeatureBuilder setEnv(Env env) {
		this.env = env;
		return this;
	}

	public EnvFeatureBuilder setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
		return this;
	}

	public EnvFeatureBuilder setMigratedDate(String migratedDate) {
		this.migratedDate = migratedDate;
		return this;
	}

	public EnvFeatureBuilder setFeature(Feature feature) {
		this.feature = feature;
		return this;
	}
	
	public EnvFeature build() {
		nullCheck("deployedDate", "feature", "env");
		EnvFeature envFeature = new EnvFeature();
		feature.addEnvFeature(envFeature);
		env.addEnvFeature(envFeature);
		envFeature.setDeployedDate(deployedDate);
		if (migratedDate != null) {
			envFeature.setMigratedDate(migratedDate);	
		} else if (env.getName().equals("green") || env.getName().equals("blue")) {
			envFeature.setMigratedDate(deployedDate);
		}
		log.info("new envFeature object - " + envFeature);
		return envFeature;
	}
	
	private void nullCheck(String...fields) {
		for (String field : fields) {
			nullCheck(field);
		}
	}
	
	private void nullCheck(String field) {
		try {
			if (this.getClass().getDeclaredField(field).get(this) == null) {
				throw new MissingFieldException(field + " cannot be null");
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
}
