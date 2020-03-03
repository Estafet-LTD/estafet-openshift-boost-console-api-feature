package com.estafet.openshift.boost.console.api.feature.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BuildEnv extends BaseEnv {

	private List<BuildApp> buildApps = new ArrayList<BuildApp>();

	public List<BuildApp> getBuildApps() {
		return buildApps;
	}

	public void setBuildApps(List<BuildApp> buildApps) {
		this.buildApps = buildApps;
	}
	
    @Override
	public List<BaseApp> getApps() {
		return new ArrayList<BaseApp>(buildApps);
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
