package com.estafet.openshift.boost.console.api.feature.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.Feature;

@Repository
public class FeatureDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public Feature getFeatureById(String featureId) {
		return entityManager.find(Feature.class, featureId);
	}

	public void create(Feature feature) {
		entityManager.persist(feature);
	}

}
