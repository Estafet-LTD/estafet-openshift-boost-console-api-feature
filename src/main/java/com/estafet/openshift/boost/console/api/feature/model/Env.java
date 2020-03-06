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

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.estafet.openshift.boost.console.api.feature.dto.EnvironmentDTO;

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

	@OneToMany(mappedBy = "env", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<EnvFeature> envFeatures = new HashSet<EnvFeature>();

	@OneToMany(mappedBy = "env", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<EnvMicroservice> envMicroservices = new HashSet<EnvMicroservice>();

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public void addMicroservice(EnvMicroservice envMicroservice) {
		if (!envMicroservices.contains(envMicroservice)) {
			envMicroservices.add(envMicroservice);
			envMicroservice.setEnv(this);
		} else {
			getMicroservice(envMicroservice.getMicroservice()).update(envMicroservice);
		}
	}

	public Set<EnvMicroservice> getMicroservices() {
		return envMicroservices;
	}

	public void setMicroservices(Set<EnvMicroservice> envMicroservices) {
		this.envMicroservices = envMicroservices;
	}

	public Set<Feature> getFeatures() {
		Set<Feature> features = new HashSet<Feature>();
		for (EnvFeature envFeature : envFeatures) {
			features.add(envFeature.getFeature());
		}
		return features;
	}

	public EnvFeature getEnvFeature(String featureId) {
		for (EnvFeature envfeature : envFeatures) {
			if (envfeature.getFeature().getFeatureId().equals(name)) {
				return envfeature;
			}
		}
		return null;
	}

	public void addEnvFeature(EnvFeature envFeature) {
		if (!getFeatures().contains(envFeature.getFeature())
				&& (name.equals("build") || envFeature.getFeature().getStatus().equals("DONE"))) {
			updatedDate = DateUtils.newDate();
			envFeatures.add(envFeature);
			envFeature.setEnv(this);
		}
	}

	public EnvMicroservice getMicroservice(String name) {
		for (EnvMicroservice envMicroservice : envMicroservices) {
			if (envMicroservice.getMicroservice().equals(name)) {
				return envMicroservice;
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

	public String getPreviousEnv() {
		if (name.equals("test")) {
			return "build";
		} else if (name.equals("green")) {
			if (live) {
				return "blue";
			} else {
				return "test";
			}
		} else if (name.equals("blue")) {
			if (live) {
				return "green";
			} else {
				return "test";
			}
		}
		return null;
	}
	
	public EnvironmentDTO getEnvironmentDTO() {
		EnvironmentDTO environmentDTO = EnvironmentDTO.builder()
				.setLive(live)
				.setName(name)
				.setUpdatedDate(updatedDate)
				.build();
		for (EnvFeature envFeature : envFeatures) {
			environmentDTO.addFeature(envFeature.getFeatureDTO());
		}
		return environmentDTO;
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
