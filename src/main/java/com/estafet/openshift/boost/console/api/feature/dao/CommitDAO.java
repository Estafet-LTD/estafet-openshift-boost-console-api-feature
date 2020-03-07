package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.Matched;

@Repository
public class CommitDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public List<Matched> getMatchedForMicroservice(String microservice) {
		TypedQuery<Matched> query = entityManager
				.createQuery("select m from Matched m where m.repo.microservice = ?1", Matched.class);
		return query.setParameter(1, microservice).getResultList();
	}

}
