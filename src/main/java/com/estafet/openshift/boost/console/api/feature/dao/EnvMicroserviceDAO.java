package com.estafet.openshift.boost.console.api.feature.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.EnvMicroservice;

@Repository
public class EnvMicroserviceDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public void createEnv(EnvMicroservice envMicroservice) {
		entityManager.persist(envMicroservice);	
	}
	
}
