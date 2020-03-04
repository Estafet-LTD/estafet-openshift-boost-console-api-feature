package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.Feature;

@Repository
public class FeatureDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public Feature getFeatureByCommit(String repo, String sha) {
		try {
			TypedQuery<Feature> query = entityManager
					.createQuery("select DISTINCT f from Feature f JOIN f.matched m where m.repo.name = ?1 and m.sha = ?2", Feature.class);
			return query.setParameter(1, repo).setParameter(2, sha).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Feature> getFeaturesByRepo(String repo) {
		TypedQuery<Feature> query = entityManager
				.createQuery("select DISTINCT f from Feature f JOIN f.matched m where m.repo.name = ?1", Feature.class);
		return query.setParameter(1, repo).getResultList();
	}

	public Feature getFeatureById(String featureId) {
		return entityManager.find(Feature.class, featureId);
	}

	public void create(Feature feature) {
		entityManager.persist(feature);
	}

}
