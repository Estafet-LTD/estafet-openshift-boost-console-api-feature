package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.List;

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

	@SuppressWarnings("unchecked")
	public List<Repo> getRepos() {
		return entityManager.createQuery("Select r from Repo r").getResultList();
	}

}
