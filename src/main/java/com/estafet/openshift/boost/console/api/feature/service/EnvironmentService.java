package com.estafet.openshift.boost.console.api.feature.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.dto.EnvironmentDTO;
import com.estafet.openshift.boost.console.api.feature.message.BaseApp;
import com.estafet.openshift.boost.console.api.feature.message.BaseEnv;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.Microservice;

@Service
public class EnvironmentService {

	private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);
	
	@Autowired
	private EnvDAO envDAO;
	
	@Autowired
	private RepoDAO repoDAO;
	
	@Transactional
	public void updateEnv(BaseEnv envMessage) {
		log.info("updateEnv for env - " + envMessage.getName());
		Map<String, String> reposMap = repoDAO.reposMap();
		Env env = envDAO.getEnv(envMessage.getName());
		if (env == null) {
			env = new Env();
			env.setLive(envMessage.isLive());
			env.setName(envMessage.getName());
			env.setUpdatedDate(DateUtils.newDate());
			envDAO.createEnv(env);
		}
		for (BaseApp app : envMessage.getApps()) {
			Microservice microservice = Microservice.builder()
							.setDeployedDate(app.getDeployedDate())
							.setName(app.getName())
							.setRepo(reposMap.get(app.getName()))
							.setVersion(app.getVersion())
							.build();
			env.addMicroservice(microservice);
		}
		envDAO.updateEnv(env);
	}

	@Transactional(readOnly = true)
	public EnvironmentDTO getEnv(String env) {
		return envDAO.getEnv(env).getEnvironmentDTO();
	}
	
}
