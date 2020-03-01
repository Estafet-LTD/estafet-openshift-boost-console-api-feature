package com.estafet.openshift.boost.console.api.feature.model;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BuildEnv {

	private String name;

	private String updatedDate;

	private List<BuildApp> buildApps;

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

	public List<BuildApp> getBuildApps() {
		return buildApps;
	}

	public void setBuildApps(List<BuildApp> buildApps) {
		this.buildApps = buildApps;
	}
	
    public static BuildEnv fromJSON(String message) {
        try {
            return new ObjectMapper().readValue(message, BuildEnv.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJSON() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
