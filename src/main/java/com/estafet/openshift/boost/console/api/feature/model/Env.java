package com.estafet.openshift.boost.console.api.feature.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.estafet.openshift.boost.console.api.feature.dto.EnvironmentDTO;
import com.estafet.openshift.boost.console.api.feature.dto.FeatureDTOComparator;
import com.estafet.openshift.boost.messages.environments.Environment;
import com.estafet.openshift.boost.messages.environments.EnvironmentApp;

@Entity
@Table(name = "ENV")
public class Env {

	private static final Logger log = LoggerFactory.getLogger(Env.class);

	@Id
	@Column(name = "ENV_ID", nullable = false)
	private String name;

	@Column(name = "DISPLAY", nullable = false)
	private String displayName;
	
	@Column(name = "UPDATED_DATE", nullable = false)
	private String updatedDate;

	@Column(name = "LIVE", nullable = true)
	private Boolean live;
	
	@Column(name = "TESTED", nullable = true)
	private Boolean tested;
	
	@Column(name = "NEXT", nullable = true)
	private String next;

	@OneToMany(mappedBy = "env", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EnvFeature> envFeatures = new HashSet<EnvFeature>();

	@OneToMany(mappedBy = "env", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<EnvMicroservice> envMicroservices = new HashSet<EnvMicroservice>();

	public Boolean getTested() {
		return tested;
	}

	public void setTested(Boolean tested) {
		this.tested = tested;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public Boolean getLive() {
		return live;
	}

	public void setLive(Boolean live) {
		this.live = live;
	}
	
	public Env merge(Env other) {
		this.displayName = other.displayName;
		this.live = other.live;
		this.next = other.next;
		this.tested = other.tested;
		this.updatedDate = other.updatedDate;
		return this;
	}

	public void updateMicroservice(EnvironmentApp app, Repo repo) {
		if (getMicroservice(app.getName()) == null) {
			EnvMicroservice envMicroservice = EnvMicroservice.builder()
					.setDeployedDate(app.getDeployedDate())
					.setVersion(app.getVersion())
					.setMicroservice(app.getName())
					.build();
			envMicroservices.add(envMicroservice);
			envMicroservice.setEnv(this);
			log.info("added - " + app.getName());
		} else {
			EnvMicroservice envMicroservice = getMicroservice(app.getName());
			if (!envMicroservice.getVersion().equals(app.getVersion())) {
				envMicroservice.setVersion(app.getVersion());
				log.info("version updated - " + app.getName());
			}
			if (!envMicroservice.getDeployedDate().equals(app.getDeployedDate())) {
				envMicroservice.setDeployedDate(app.getDeployedDate());
				log.info("deployed date updated - " + app.getName());
			}
		}
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Set<EnvFeature> getEnvFeatures() {
		return envFeatures;
	}

	public void setEnvFeatures(Set<EnvFeature> envFeatures) {
		this.envFeatures = envFeatures;
	}

	public Set<EnvMicroservice> getEnvMicroservices() {
		return envMicroservices;
	}

	public void setEnvMicroservices(Set<EnvMicroservice> envMicroservices) {
		this.envMicroservices = envMicroservices;
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
			if (envfeature.getFeature().getFeatureId().equals(featureId)) {
				return envfeature;
			}
		}
		return null;
	}

	public void addEnvFeature(EnvFeature envFeature) {
		envFeatures.add(envFeature);
		envFeature.setEnv(this);
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

	public EnvironmentDTO getEnvironmentDTO() {
		EnvironmentDTO dto = EnvironmentDTO.builder()
								.setLive(live)
								.setTested(tested)
								.setDisplayName(displayName)
								.setName(name)
								.setUpdatedDate(updatedDate)
								.build();
		for (EnvFeature envFeature : envFeatures) {
			dto.addFeature(envFeature.getFeatureDTO());
		}
		Collections.sort(dto.getFeatures(), new FeatureDTOComparator());
		return dto;
	}
	
	public static Env getEnv(Environment envMessage) {
		return Env.builder()
				.setLive(envMessage.getLive())
				.setNext(envMessage.getNext())
				.setTested(envMessage.getTested())
				.setUpdatedDate(envMessage.getUpdatedDate())
				.setDisplayName(envMessage.getDisplayName())
				.setName(envMessage.getName())
				.build();
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

	public static EnvBuilder builder() {
		return new EnvBuilder();
	}

	@Override
	public String toString() {
		return "Env [name=" + name + ", updatedDate=" + updatedDate + ", live=" + live + ", tested=" + tested
				+ ", next=" + next + "]";
	}
	
	public static class EnvBuilder {

		private String name;
		private String displayName;
		private Boolean live;
		private String updatedDate;
		private Boolean tested;
		private String next;
		
		private EnvBuilder() {}

		public EnvBuilder setDisplayName(String displayName) {
			this.displayName = displayName;
			return this;
		}

		public EnvBuilder setTested(Boolean tested) {
			this.tested = tested;
			return this;
		}

		public EnvBuilder setNext(String next) {
			this.next = next;
			return this;
		}

		public EnvBuilder setUpdatedDate(String updatedDate) {
			this.updatedDate = updatedDate;
			return this;
		}

		public EnvBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public EnvBuilder setLive(Boolean live) {
			this.live = live;
			return this;
		}
		
		public Env build() {
			Env env = new Env();
			env.setLive(live);
			env.setName(name);
			env.setDisplayName(displayName);
			env.setUpdatedDate(updatedDate);
			env.setNext(next);
			env.setTested(tested);
			return env;
		}
		
	}

}
