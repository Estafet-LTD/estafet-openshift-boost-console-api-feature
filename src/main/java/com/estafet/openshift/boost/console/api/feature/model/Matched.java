package com.estafet.openshift.boost.console.api.feature.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("MATCHED")
public class Matched extends RepoCommit {

	@ManyToOne
	@JoinColumn(name = "FEATURE_ID", nullable = true, referencedColumnName = "FEATURE_ID", foreignKey = @ForeignKey(name = "COMMIT_TO_FEATURE_FK"))
	private Feature feature;
	
	@Column(name = "VERSION", nullable = true)
	private String version;
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	
}
