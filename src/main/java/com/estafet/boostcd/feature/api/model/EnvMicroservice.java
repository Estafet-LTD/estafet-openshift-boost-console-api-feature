package com.estafet.boostcd.feature.api.model;

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

@Entity
@Table(name = "ENV_MICROSERVICE", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"ENV_ID", "MICROSERVICE"}, name = "ENV_MICROSERVICE_KEY") })
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

	@Column(name = "DEPLOYED_DATE", nullable = true)
	private String deployedDate;

	@ManyToOne
	@JoinColumn(name = "ENV_ID", nullable = false, referencedColumnName = "ENV_ID", foreignKey = @ForeignKey(name = "MICROSERVICE_TO_ENV_FK"))
	private Env env;

	public String getDeployedDate() {
		return deployedDate;
	}

	public void setDeployedDate(String deployedDate) {
		this.deployedDate = deployedDate;
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
		EnvMicroservice other = (EnvMicroservice) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static EnvMicroserviceBuilder builder() {
		return new EnvMicroserviceBuilder();
	}

	public void update(EnvMicroservice envMicroservice) {
		this.version = envMicroservice.version;
		this.deployedDate = envMicroservice.deployedDate;
	}

	@Override
	public String toString() {
		return "EnvMicroservice [id=" + id + ", microservice=" + microservice + ", env=" + env + "]";
	}
	
	public static class EnvMicroserviceBuilder {

		private String version;

		private String deployedDate;
		
		private String microservice;
		
		private EnvMicroserviceBuilder( ) { }

		public EnvMicroserviceBuilder setMicroservice(String microservice) {
			this.microservice = microservice;
			return this;
		}

		public EnvMicroserviceBuilder setVersion(String version) {
			this.version = version;
			return this;
		}

		public EnvMicroserviceBuilder setDeployedDate(String deployedDate) {
			this.deployedDate = deployedDate;
			return this;
		}

		public EnvMicroservice build() {
			EnvMicroservice envMicroservice = new EnvMicroservice();
			envMicroservice.setMicroservice(microservice);
			envMicroservice.setDeployedDate(deployedDate);
			envMicroservice.setVersion(version);
			return envMicroservice;
		}
	}
	
	public boolean isGreaterOrEqualThan(EnvMicroservice other) {
		if (microservice.equals(other.microservice)) {
			return new Version(version).isGreaterOrEqualThan(new Version(other.version));
		}
		throw new RuntimeException("Canot compare " + microservice + " with " + other);
	}
	
	public boolean isLessThanOrEqual(EnvMicroservice other) {
		if (microservice.equals(other.microservice)) {
			return new Version(version).isLessThanOrEqual(new Version(other.version));
		}
		throw new RuntimeException("Canot compare " + microservice + " with " + other);
	}

}
