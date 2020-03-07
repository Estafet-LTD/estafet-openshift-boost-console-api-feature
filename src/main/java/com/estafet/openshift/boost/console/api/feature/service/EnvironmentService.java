package com.estafet.openshift.boost.console.api.feature.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.dto.EnvironmentDTO;
import com.estafet.openshift.boost.console.api.feature.message.BaseApp;
import com.estafet.openshift.boost.console.api.feature.message.BaseEnv;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.console.api.feature.model.EnvMicroservice;

@Service
public class EnvironmentService {

	private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);

	@Autowired
	private EnvDAO envDAO;
	
	@Autowired
	private RepoDAO repoDAO;

	@Transactional
	public boolean createEnv(BaseEnv envMessage) {
		Env env = envDAO.getEnv(envMessage.getName());
		if (env == null) {
			env = Env.builder()
					.setLive(envMessage.isLive())
					.setUpdatedDate(envMessage.getUpdatedDate())
					.setName(envMessage.getName())
					.build();
			envDAO.createEnv(env);	
			log.info("created env - " + envMessage.getName());
			return true;
		} else {
			log.info("already exists env - " + envMessage.getName());
			return !env.getUpdatedDate().equals(envMessage.getUpdatedDate());
		}
	}

	@Transactional(readOnly = true)
	public EnvironmentDTO getEnv(String env) {
		return envDAO.getEnv(env).getEnvironmentDTO();
	}
	
	@Transactional
	private void updateMicroservices(BaseEnv envMessage) {
		log.info("updateMicroservices for env - " + envMessage.getName());
		Env env = envDAO.getEnv(envMessage.getName());
		for (BaseApp app : envMessage.getApps()) {
			log.info("create envMicroservice for " + app.getName());
			EnvMicroservice.builder()
					.setDeployedDate(app.getDeployedDate())
					.setRepo(repoDAO.getRepoByMicroservice(app.getName()))
					.setEnv(env)
					.setVersion(app.getVersion())
					.build();
		}
		envDAO.updateEnv(env);
		log.info("Microservices successfully updated for env - " + env.getName());
	}

}
