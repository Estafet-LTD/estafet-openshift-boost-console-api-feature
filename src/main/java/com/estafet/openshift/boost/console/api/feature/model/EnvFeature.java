package com.estafet.openshift.boost.console.api.feature.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.estafet.openshift.boost.console.api.feature.dto.FeatureDTO;
import com.estafet.openshift.boost.messages.features.MissingFieldException;

@Entity
@Table(name = "ENV_FEATURE", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"ENV_ID", "FEATURE_ID"}, name = "ENV_FEATURE_KEY") })
public class EnvFeature {

	private static final Logger log = LoggerFactory.getLogger(EnvFeature.class);
	
	@Id
	@SequenceGenerator(name = "ENV_FEATURE_SEQ", sequenceName = "ENV_FEATURE_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENV_FEATURE_SEQ")
	@Column(name = "ENV_FEATURE_ID")
	private Long id;

	@Column(name = "DEPLOYED_DATE", nullable = false)
	private String deployedDate;

	@Column(name = "MIGRATED_DATE", nullable = true)
	private String migratedDate;
	
	@Column(name = "PARTIAL", nullable = true)
	private Boolean partial;

	@ManyToOne
	@JoinColumn(name = "FEATURE_ID", nullable = false, referencedColumnName = "FEATURE_ID", foreignKey = @ForeignKey(name = "ENV_FEATURE_TO_FEATURE_FK"))
	private Feature feature;

	@ManyToOne
	@JoinColumn(name = "ENV_ID", nullable = false, referencedColumnName = "ENV_ID", foreignKey = @ForeignKey(name = "ENV_FEATURE_TO_ENV_FK"))
	private Env env;
	
	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "ENV_FEATURE_MICROSERVICE",
		joinColumns = @JoinColumn(name = "ENV_FEATURE_ID", foreignKey = @ForeignKey(name = "ENV_FEATURE_ID_ENV_FEATURE_MS_FK")),
		inverseJoinColumns = @JoinColumn(name = "ENV_MICROSERVICE_ID", foreignKey = @ForeignKey(name = "ENV_MS_ID_ENV_FEATURE_MS_FK"))
	)
	private Set<EnvMicroservice> microservices = new HashSet<EnvMicroservice>();

	public void addMicroservice(EnvMicroservice envMicroservice) {
		microservices.add(envMicroservice);
		envMicroservice.getFeatures().add(this);
	}
	
	public Boolean isPartial() {
		return partial;
	}

	public void setPartial(Boolean partial) {
		this.partial = partial;
	}

	public Set<EnvMicroservice> getMicroservices() {
		return microservices;
	}

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
	
	@Override
	public String toString() {
		return "EnvFeature [id=" + id + ", deployedDate=" + deployedDate + ", migratedDate=" + migratedDate
				+ ", feature=" + feature + ", env=" + env + "]";
	}

	public String calculateDeployedDate() {
		Set<RepoCommit> matches = getFeature().getMatched();
		String minDeployedDate = null;
		for (RepoCommit matched : matches) {
			String microservice = matched.getRepo().getMicroservice();
			EnvMicroservice envMicroservice = env.getMicroservice(microservice);
			Date deployedDate = DateUtils.getDate(envMicroservice.getDeployedDate());
			minDeployedDate = minDeployedDate == null
					|| deployedDate.before(DateUtils.getDate(minDeployedDate))
							? envMicroservice.getDeployedDate()
							: minDeployedDate;
		}
		log.info("calculateDeployedDate - " + minDeployedDate);
		return minDeployedDate;
	}
	
	public static EnvFeatureBuilder builder() {
		return new EnvFeatureBuilder();
	}

	public FeatureDTO getFeatureDTO() {
		return FeatureDTO.builder()
				.setUrl(feature.getUrl())
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

	public static class EnvFeatureBuilder {
		
		private EnvMicroservice envMicroservice;
		private Feature feature;

		private EnvFeatureBuilder() { }
		
		public EnvFeatureBuilder setEnvMicroservice(EnvMicroservice envMicroservice) {
			this.envMicroservice = envMicroservice;
			return this;
		}

		public EnvFeatureBuilder setFeature(Feature feature) {
			this.feature = feature;
			return this;
		}
		
		public EnvFeature build() {
			nullCheck("envMicroservice", "feature");
			EnvFeature envFeature = new EnvFeature();
			feature.addEnvFeature(envFeature);
			envMicroservice.getEnv().addEnvFeature(envFeature);
			envFeature.addMicroservice(envMicroservice);
			envFeature.setDeployedDate(envMicroservice.getDeployedDate());
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

	public boolean isPartiallyPromoted(EnvFeature prevEnvFeature) {
		for (EnvMicroservice prevEnvMicroservice : prevEnvFeature.getMicroservices()) {
			if (!containsEnvMicroservice(prevEnvMicroservice)) {
				return true;
			}
		}
		return false;
	}
	
	private EnvMicroservice getEnvMicroservice(String name) {
		for (EnvMicroservice envMicroservice : microservices) {
			if (envMicroservice.getMicroservice().equals(name)) {
				return envMicroservice;
			}
		}
		return null;
	}

	private boolean containsEnvMicroservice(EnvMicroservice prevEnvMicroservice) {
		EnvMicroservice envMicroservice = getEnvMicroservice(prevEnvMicroservice.getMicroservice());
		if (envMicroservice == null) {
			return false;	
		}
		Version version = new Version(envMicroservice.getVersion());
		Version prevVersion = new Version(prevEnvMicroservice.getVersion());
		if (version.isLessThan(prevVersion)) {
			return false;
		} else {
			return true;
		}
	}
	
}
