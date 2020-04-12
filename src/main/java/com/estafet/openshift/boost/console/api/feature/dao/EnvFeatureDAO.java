package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.EnvFeature;

@Repository
public class EnvFeatureDAO {

	private static final Logger log = LoggerFactory.getLogger(EnvFeatureDAO.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	public void create(EnvFeature envFeature) {
		log.info("create envFeature - " + envFeature);
		entityManager.persist(envFeature);	
	}
	
	public void update(EnvFeature envFeature) {
		log.info("update envFeature - " + envFeature);
		entityManager.merge(envFeature);	
	}
	
	public List<EnvFeature> getNewEnvFeatures(String env) {
		TypedQuery<EnvFeature> query = entityManager
				.createQuery("select f from EnvFeature f where f.env.name = :env and f.migratedDate IS NULL", EnvFeature.class);
		return query.setParameter("env", env).getResultList();
	}
	
}
