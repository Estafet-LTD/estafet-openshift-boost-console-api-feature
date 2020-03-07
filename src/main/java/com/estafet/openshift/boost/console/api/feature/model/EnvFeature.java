package com.estafet.openshift.boost.console.api.feature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.estafet.openshift.boost.console.api.feature.dto.FeatureDTO;
import com.estafet.openshift.boost.console.api.feature.message.EnvFeatureMessage;

@Entity
@IdClass(EnvFeatureId.class)
@Table(name = "ENV_FEATURE")
public class EnvFeature {
	
	@Column(name = "DEPLOYED_DATE", nullable = false)
	private String deployedDate;

	@Column(name = "MIGRATED_DATE", nullable = true)
	private String migratedDate;

	@Id
	@ManyToOne
	@JoinColumn(name = "FEATURE_ID", nullable = false, referencedColumnName = "FEATURE_ID", foreignKey = @ForeignKey(name = "ENV_FEATURE_TO_FEATURE_FK"))
	private Feature feature;

	@Id
	@ManyToOne
	@JoinColumn(name = "ENV_ID", nullable = false, referencedColumnName = "ENV_ID", foreignKey = @ForeignKey(name = "ENV_FEATURE_TO_ENV_FK"))
	private Env env;

	public String getDeployedDate() {
		return deployedDate;
	}

	public void setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
	}

	public String getMigratedDate() {
		return migratedDate;
	}

	public void setMigratedDate(String migratedDate) {
		this.migratedDate = migratedDate;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Env getEnv() {
		return env;
	}

	public void setEnv(Env env) {
		this.env = env;
	}

	public static EnvFeatureBuilder builder() {
		return new EnvFeatureBuilder();
	}

	public EnvFeatureMessage getEnvFeatureMessage() {
		return EnvFeatureMessage.builder()
				.setDeployedDate(deployedDate)
				.setDescription(feature.getDescription())
				.setDeployedDate(deployedDate)
				.setEnvironment(env.getName())
				.setFeatureId(feature.getFeatureId())
				.setMigratedDate(migratedDate)
				.setStatus(feature.getStatus())
				.setTitle(feature.getTitle())
				.build();
	}

	public FeatureDTO getFeatureDTO() {
		return FeatureDTO.builder()
				.setDescription(feature.getDescription())
				.setFeatureId(feature.getFeatureId())
				.setStatus(feature.getStatus())
				.setTitle(feature.getTitle())
				.setPromoted(migratedDate != null)
				.setUnpromotedSince(migratedDate == null ? deployedDate : null)
				.build();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((env == null) ? 0 : env.hashCode());
		result = prime * result + ((feature == null) ? 0 : feature.hashCode());
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
		EnvFeature other = (EnvFeature) obj;
		if (env == null) {
			if (other.env != null)
				return false;
		} else if (!env.equals(other.env))
			return false;
		if (feature == null) {
			if (other.feature != null)
				return false;
		} else if (!feature.equals(other.feature))
			return false;
		return true;
	}

}
