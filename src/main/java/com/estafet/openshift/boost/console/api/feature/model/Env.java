package com.estafet.openshift.boost.console.api.feature.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ENV")
public class Env {

	@Id
	@Column(name = "ENV_ID", nullable = false)
	private String name;

	@Column(name = "UPDATED_DATE", nullable = false)
	private String updatedDate;

	@Column(name = "LIVE", nullable = false)
	private boolean live = false;

	@OneToMany(mappedBy = "env", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EnvFeature> envFeatures = new HashSet<EnvFeature>();

	@OneToMany(mappedBy = "env", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Microservice> microservices = new HashSet<Microservice>();

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public void addMicroservice(Microservice microservice) {
		if (!microservices.contains(microservice)) {
			microservices.add(microservice);
			microservice.setEnv(this);
		} else {
			getMicroservice(microservice.getName()).update(microservice);
		}
	}

	public Set<Microservice> getMicroservices() {
		return microservices;
	}

	public void setMicroservices(Set<Microservice> microservices) {
		this.microservices = microservices;
	}

	public Set<Feature> getFeatures() {
		Set<Feature> features = new HashSet<Feature>();
		for (EnvFeature envFeature : envFeatures) {
			features.add(envFeature.getFeature());
		}
		return features;
	}

	public void addEnvFeature(EnvFeature envFeature) {
		if (!getFeatures().contains(envFeature.getFeature())) {
			envFeatures.add(envFeature);
			envFeature.setEnv(this);
		}
	}

	public Microservice getMicroservice(String name) {
		for (Microservice microservice : microservices) {
			if (microservice.getName().equals(name)) {
				return microservice;
			}
		}
		return null;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Env other = (Env) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
