package com.estafet.openshift.boost.console.api.feature.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.model.BuildEnv;
import com.estafet.openshift.boost.console.api.feature.model.Env;

@Service
public class EnvironmentService {

	@Autowired
	private EnvDAO envDAO;
	
	@Transactional
	public void updateEnv(BuildEnv buildEnv) {
		Env env = envDAO.getEnvByName("build");
		if (env == null) {
			env = new Env();
			env.setLive(false);
			env.setName("build");
			//env.setUpdatedDate(updatedDate);
		}
	}
	
	@Transactional(readOnly = true)
	public Env getEnv(String env) {
		return envDAO.getEnvByName(env);
	}
	
}
