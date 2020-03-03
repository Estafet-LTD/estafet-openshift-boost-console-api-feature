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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "MICROSERVICE")
public class Microservice {

	@Id
	@SequenceGenerator(name = "MICROSERVICE_ID_SEQ", sequenceName = "MICROSERVICE_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MICROSERVICE_ID_SEQ")
	@Column(name = "MICROSERVICE_ID")
	private Long id;

	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "VERSION", nullable = false)
	private String version;

	@Column(name = "REPO", nullable = false)
	private String repo;
	
	@Column(name = "DEPLOYED_DATE", nullable = false)
	private String deployedDate;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "ENV_ID", nullable = false, referencedColumnName = "ENV_ID", foreignKey = @ForeignKey(name = "MICROSERVICE_TO_ENV_FK"))
	private Env env;

	public String getDeployedDate() {
		return deployedDate;
	}

	public void setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
	}

	public String getRepo() {
		return repo;
	}

	public void setRepo(String repo) {
		this.repo = repo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		Microservice other = (Microservice) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public static MicroserviceBuilder builder() {
		return new MicroserviceBuilder();
	}

	public void update(Microservice microservice) {
		this.version = microservice.version;
		this.repo = microservice.repo;
		this.deployedDate = microservice.deployedDate;
	}

}
