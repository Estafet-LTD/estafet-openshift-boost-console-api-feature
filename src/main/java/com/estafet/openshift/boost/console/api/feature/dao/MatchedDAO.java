package com.estafet.openshift.boost.console.api.feature.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.Matched;

@Repository
public class MatchedDAO {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Matched getMatched(String commitId) {
		return entityManager.find(Matched.class, commitId);
	}
	
}
