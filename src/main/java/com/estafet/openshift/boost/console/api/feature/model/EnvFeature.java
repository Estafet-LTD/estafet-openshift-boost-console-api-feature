package com.estafet.openshift.boost.console.api.feature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.estafet.openshift.boost.console.api.feature.dto.FeatureDTO;

@Entity
@Table(name = "ENV_FEATURE", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"ENV_ID", "FEATURE_ID"}, name = "ENV_FEATURE_KEY") })
public class EnvFeature {

	@Id
	@SequenceGenerator(name = "ENV_FEATURE_SEQ", sequenceName = "ENV_FEATURE_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENV_FEATURE_SEQ")
	@Column(name = "ENV_FEATURE_ID")
	private Long id;

	@Column(name = "DEPLOYED_DATE", nullable = false)
	private String deployedDate;

	@Column(name = "MIGRATED_DATE", nullable = true)
	private String migratedDate;

	@ManyToOne
	@JoinColumn(name = "FEATURE_ID", nullable = false, referencedColumnName = "FEATURE_ID", foreignKey = @ForeignKey(name = "ENV_FEATURE_TO_FEATURE_FK"))
	private Feature feature;

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

	public FeatureDTO getFeatureDTO() {
		return FeatureDTO.builder()
				.setDescription(feature.getDescription())
				.setFeatureId(feature.getFeatureId())
				.setStatus(feature.getStatus())
				.setTitle(feature.getTitle())
				.setPromoted(migratedDate != null)
				.setWaitingSince(migratedDate == null ? deployedDate : null)
				.build();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
