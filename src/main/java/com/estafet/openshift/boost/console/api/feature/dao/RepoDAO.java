package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public Map<String, Repo> microservicesReposMap() {
		Map<String, Repo> microservices = new HashMap<String, Repo>();
		List<Repo> repos = getRepos();
		for (Repo repo : repos) {
			microservices.put(repo.getMicroservice(), repo);
		}
		return microservices;
	}

}
