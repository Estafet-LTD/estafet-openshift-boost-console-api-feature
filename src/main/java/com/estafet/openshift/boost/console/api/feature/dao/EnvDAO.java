package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.Env;

@Repository
public class EnvDAO {

	@PersistenceContext
	private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public List<Env> getEnvs() {
		return entityManager.createQuery("Select e from Env e").getResultList();
	}
	
	public Env getEnv(String envId) {
		return entityManager.find(Env.class, envId);
	}
	
	public Env getLiveEnv() {
		TypedQuery<Env> query = entityManager
				.createQuery("Select e from Env e where e.live = TRUE", Env.class);
		List<Env> envs = query.getResultList();
		if (!envs.isEmpty()) {
			return envs.get(0);
		} else {
			return null;
		}
	}
	
	public Env getStagingEnv() {
		TypedQuery<Env> query = entityManager
				.createQuery("Select e from Env e where e.live = FALSE and e.name IN ('green', 'blue')", Env.class);
		List<Env> envs = query.getResultList();
		if (!envs.isEmpty()) {
			return envs.get(0);
		} else {
			return null;
		}
	}

	public void updateEnv(Env env) {
		entityManager.merge(env);
	}

	public void createEnv(Env env) {
		entityManager.persist(env);	
	}
	
}
