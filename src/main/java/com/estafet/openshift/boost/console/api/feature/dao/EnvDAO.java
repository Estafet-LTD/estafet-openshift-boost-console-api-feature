package com.estafet.openshift.boost.console.api.feature.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.Env;

@Repository
public class EnvDAO {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Env getEnv(String envId) {
		return entityManager.find(Env.class, envId);
	}

	public void updateEnv(Env env) {
		entityManager.merge(env);
	}

	public void createEnv(Env env) {
		entityManager.persist(env);	
	}
	
}
