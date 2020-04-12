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
				"select f from Feature f JOIN f.matched m where m.repo.name = :repo and m.sha = :sha", Feature.class);
		return query.setParameter("repo", repo).setParameter("sha", sha).getResultList();
	}
	
	public List<Feature> getIncompleteFeatures() {
		TypedQuery<Feature> query = entityManager.createQuery(
				"select f from Feature f where f.status <> :status", Feature.class);
		return query.setParameter("status", "Done").getResultList();
	}

	public void create(Feature feature) {
		entityManager.persist(feature);
	}
	
	public void update(Feature feature) {
		entityManager.merge(feature);
	}

}
