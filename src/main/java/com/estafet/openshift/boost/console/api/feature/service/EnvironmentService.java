package com.estafet.openshift.boost.console.api.feature.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.feature.dao.RepoDAO;
import com.estafet.openshift.boost.console.api.feature.dto.EnvironmentDTO;
import com.estafet.openshift.boost.console.api.feature.model.Env;
import com.estafet.openshift.boost.messages.environments.Environment;
import com.estafet.openshift.boost.messages.environments.EnvironmentApp;

@Service
public class EnvironmentService {

	private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);

	@Autowired
	private EnvDAO envDAO;

	@Autowired
	private RepoDAO repoDAO;

	@Transactional
	public boolean createEnv(Environment envMessage) {
		Env env = envDAO.getEnv(envMessage.getName());
		if (env == null) {
			env = Env.builder().setLive(envMessage.isLive()).setUpdatedDate(envMessage.getUpdatedDate())
					.setName(envMessage.getName()).build();
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
	public void updateMicroservices(Environment envMessage) {
		log.info("updateMicroservices for env - " + envMessage.getName());
		Env env = envDAO.getEnv(envMessage.getName());
		for (EnvironmentApp app : envMessage.getApps()) {
			log.info("create envMicroservice for " + app.getName());
			env.updateMicroservice(app, repoDAO.getRepoByMicroservice(app.getName()));
		}
		env.setUpdatedDate(envMessage.getUpdatedDate()); // reset the date
		envDAO.updateEnv(env);
	}

}
