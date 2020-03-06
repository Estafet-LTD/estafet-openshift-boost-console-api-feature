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

@Entity
@Table(name = "ENV_MICROSERVICE")
public class EnvMicroservice {

	@Id
	@SequenceGenerator(name = "ENV_MICROSERVICE_ID_SEQ", sequenceName = "ENV_MICROSERVICE_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENV_MICROSERVICE_ID_SEQ")
	@Column(name = "ENV_MICROSERVICE_ID")
	private Long id;

	@Column(name = "MICROSERVICE", nullable = false)
	private String microservice;

	@Column(name = "VERSION", nullable = false)
	private String version;

	@Column(name = "DEPLOYED_DATE", nullable = false)
	private String deployedDate;

	@ManyToOne
	@JoinColumn(name = "ENV_ID", nullable = false, referencedColumnName = "ENV_ID", foreignKey = @ForeignKey(name = "MICROSERVICE_TO_ENV_FK"))
	private Env env;

	@ManyToOne
	@JoinColumn(name = "REPO_ID", nullable = false, referencedColumnName = "REPO_ID", foreignKey = @ForeignKey(name = "MICROSERVICE_TO_REPO_FK"))
	private Repo repo;

	public String getDeployedDate() {
		return deployedDate;
	}

	public void setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
	}

	public Repo getRepo() {
		return repo;
	}

	public void setRepo(Repo repo) {
		this.repo = repo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMicroservice() {
		return microservice;
	}

	public void setMicroservice(String microservice) {
		this.microservice = microservice;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Env getEnv() {
		return env;
	}

	public void setEnv(Env microserviceEnv) {
		this.env = microserviceEnv;
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
		EnvMicroservice other = (EnvMicroservice) obj;
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

	public static EnvMicroserviceBuilder builder() {
		return new EnvMicroserviceBuilder();
	}

	public void update(EnvMicroservice envMicroservice) {
		this.version = envMicroservice.version;
		this.repo = envMicroservice.repo;
		this.deployedDate = envMicroservice.deployedDate;
	}

}
