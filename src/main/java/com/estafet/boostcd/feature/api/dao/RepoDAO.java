package com.estafet.boostcd.feature.api.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.estafet.boostcd.feature.api.model.Repo;

@Repository
public class RepoDAO {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Repo getRepoByURL(String url) {
		TypedQuery<Repo> query = entityManager.createQuery("Select r from Repo r where r.url = :url", Repo.class);
		List<Repo> repos = query.setParameter("url", url).getResultList();
		return repos.isEmpty() ? null : repos.get(0);
	}

	public Repo getRepo(String repoId) {
		return entityManager.find(Repo.class, repoId);
	}

	public void createRepo(Repo repo) {
		entityManager.persist(repo);
	}

	public void updateRepo(Repo repo) {
		entityManager.merge(repo);
	}

	@SuppressWarnings("unchecked")
	public List<Repo> getRepos() {
		return entityManager.createQuery("Select r from Repo r").getResultList();
	}
	
	public Repo getRepoByMicroservice(String microservice) {
		TypedQuery<Repo> query = entityManager
				.createQuery("select r from Repo r where r.microservice = :microservice", Repo.class);
		return query.setParameter("microservice", microservice).getSingleResult();
	}

}
