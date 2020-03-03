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
	
	public Env getEnv(String envId) {
		return entityManager.find(Env.class, envId);
	}
	
	public Env getEnvByName(String name) {
		TypedQuery<Env> query = entityManager
				.createQuery("select e from Env e INNER JOIN FETCH e.features f where e.name = ?1", Env.class);
		return query.setParameter(1, name).getSingleResult();
	}
	
	public List<Env> getEnvs() {
		return null;
	}

	public void updateEnv(Env env) {
		entityManager.merge(env);
	}

	public void createEnv(Env env) {
		entityManager.persist(env);	
	}
	
}
