package com.estafet.openshift.boost.console.api.feature.message;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestEnv {

	private String name;

	private boolean tested;

	private List<TestApp> testApps;

	private String updatedDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isTested() {
		return tested;
	}

	public void setTested(boolean tested) {
		this.tested = tested;
	}

	public List<TestApp> getTestApps() {
		return testApps;
	}

	public void setTestApps(List<TestApp> testApps) {
		this.testApps = testApps;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	
    public static TestEnv fromJSON(String message) {
        try {
            return new ObjectMapper().readValue(message, TestEnv.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
