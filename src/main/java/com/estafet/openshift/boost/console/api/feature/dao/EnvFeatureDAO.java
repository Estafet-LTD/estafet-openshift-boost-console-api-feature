package com.estafet.openshift.boost.console.api.feature.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;

@Repository
public class EnvFeatureDAO {

	private static final Logger log = LoggerFactory.getLogger(EnvFeatureDAO.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	public void save(EnvFeature envFeature) {
		log.info("envFeature object - " + envFeature);
		entityManager.merge(envFeature);	
	}
	
}
