package com.estafet.openshift.boost.console.api.feature.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.Env;

@Repository
public class EnvDAO {

	private static final Logger log = LoggerFactory.getLogger(EnvDAO.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public Env getEnv(String envId) {
		return entityManager.find(Env.class, envId);
	}

	public void updateEnv(Env env) {
		entityManager.merge(env);
	}

	public void createEnv(Env env) {
		log.info("create env - " + env.getName());
		entityManager.persist(env);	
	}
	
}
