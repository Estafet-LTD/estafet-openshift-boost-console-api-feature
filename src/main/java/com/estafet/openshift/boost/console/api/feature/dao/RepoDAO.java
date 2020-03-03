package com.estafet.openshift.boost.console.api.feature.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.Repo;

@Repository
public class RepoDAO {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Repo getRepo(String repoId) {
		return entityManager.find(Repo.class, repoId);
	}

	public void create(Repo repo) {
		entityManager.persist(repo);
	}

	public void update(Repo repo) {
		entityManager.merge(repo);
	}

}
