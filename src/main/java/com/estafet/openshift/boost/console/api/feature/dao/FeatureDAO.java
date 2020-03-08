package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.Feature;

@Repository
public class FeatureDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public Feature getFeatureById(String featureId) {
		return entityManager.find(Feature.class, featureId);
	}

	public List<Feature> getFeaturesByRepo(String repo, String sha) {
		TypedQuery<Feature> query = entityManager.createQuery(
				"select f from Feature f JOIN f.matched m where f.repo.name = ?1 and m.sha = ?2", Feature.class);
		return query.setParameter(1, repo).setParameter(1, sha).getResultList();
	}

	public void create(Feature feature) {
		entityManager.persist(feature);
	}

}
