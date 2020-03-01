package com.estafet.openshift.boost.console.api.feature.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.Feature;

@Repository
public class FeatureDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public Feature getFeatureByCommit(String repo, String sha) {
		TypedQuery<Feature> query = entityManager
				.createQuery("select f from Feature f where f.repo.name = ?1 and f.commitId = ?2", Feature.class);
		return query.setParameter(1, repo).setParameter(2, sha).getSingleResult();
	}

	public Feature getFeatureById(String featureId) {
		return entityManager.find(Feature.class, featureId);
	}

}
