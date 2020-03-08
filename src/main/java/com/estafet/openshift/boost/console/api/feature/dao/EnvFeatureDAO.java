package com.estafet.openshift.boost.console.api.feature.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;

@Repository
public class EnvFeatureDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public void createEnv(EnvFeature envFeature) {
		entityManager.persist(envFeature);	
	}
	
}
