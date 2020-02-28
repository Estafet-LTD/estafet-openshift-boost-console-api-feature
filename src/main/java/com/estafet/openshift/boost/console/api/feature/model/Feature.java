package com.estafet.openshift.boost.console.api.feature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "FEATURE")
public class Feature {

	@Id
	@Column(name = "FEATURE_ID", nullable = false)
	private String featureId;

	@Column(name = "TITLE", nullable = true)
	private String title;

	@Column(name = "DESCRIPTION", nullable = true)
	private String description;
	
	@Column(name = "STATUS", nullable = true)
	private String status;

	@Column(name = "UPDATED_DATE", nullable = false)
	private String updatedDate;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "ENV_ID", nullable = false, referencedColumnName = "ENV_ID", foreignKey = @ForeignKey(name = "BUILD_APP_TO_BUILD_ENV_FK"))
	private Env appEnv;

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Env getAppEnv() {
		return appEnv;
	}

	public void setAppEnv(Env appEnv) {
		this.appEnv = appEnv;
	}

	public boolean isEqualTo(Feature feature) {
		if (description == null) {
			if (feature.description != null)
				return false;
		} else if (!description.equals(feature.description))
			return false;
		if (featureId == null) {
			if (feature.featureId != null)
				return false;
		} else if (!featureId.equals(feature.featureId))
			return false;
		if (status == null) {
			if (feature.status != null)
				return false;
		} else if (!status.equals(feature.status))
			return false;
		if (title == null) {
			if (feature.title != null)
				return false;
		} else if (!title.equals(feature.title))
			return false;
		return true;
	}

	public void update(Feature recentApp) {
		this.description = recentApp.description;
		this.title = recentApp.title;
		this.status = recentApp.status;
		this.updatedDate = DateUtils.newDate();
	}

}
