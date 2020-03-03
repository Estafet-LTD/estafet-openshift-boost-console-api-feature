package com.estafet.openshift.boost.console.api.feature.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "ENV_FEATURE", joinColumns = @JoinColumn(name = "ENV_ID", foreignKey = @ForeignKey(name = "ENV_FEATURE_ENV_ID_FK")), inverseJoinColumns = @JoinColumn(name = "FEATURE_ID", foreignKey = @ForeignKey(name = "ENV_FEATURE_FEATURE_ID_FK")))
	private Set<Feature> features = new HashSet<Feature>();

	@JsonIgnore
	@OneToMany(mappedBy = "env", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Microservice> microservices = new HashSet<Microservice>();

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public void addMicroservice(Microservice microservice) {
		microservices.add(microservice);
		microservice.setEnv(this);
	}

	public Set<Microservice> getMicroservices() {
		return microservices;
	}

	public void setMicroservices(Set<Microservice> microservices) {
		this.microservices = microservices;
	}

	public void addFeature(Feature feature) {
		if (!features.contains(feature)) {
			features.add(feature);
			feature.getEnvs().add(this);	
		} else {
			getFeature(feature.getFeatureId()).update(feature);
		}
	}
	
	public Feature getFeature(String name) {
		for (Feature feature : features) {
			if (feature.getFeatureId().equals(name)) {
				return feature;
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

	public void setFeatures(Set<Feature> features) {
		this.features = features;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Set<Feature> getFeatures() {
		return features;
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
