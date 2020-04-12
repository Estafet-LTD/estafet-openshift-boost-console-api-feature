package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.RepoCommit;

@Repository
public class CommitDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public void createRepoCommit(RepoCommit commit) {
		entityManager.persist(commit);
	}
	
	public void updateRepoCommit(RepoCommit commit) {
		entityManager.merge(commit);
	}
	
	public List<RepoCommit> getMatchedForMicroservice(String microservice) {
		TypedQuery<RepoCommit> query = entityManager
				.createNamedQuery("getMatchedForMicroservice", RepoCommit.class);
		return query.setParameter("microservice", microservice).getResultList();
	}
	
	public RepoCommit getCommit(String repo, String sha) {
		TypedQuery<RepoCommit> query = entityManager
				.createNamedQuery("getCommit", RepoCommit.class);
		List<RepoCommit> commits = query.setParameter("repo", repo).setParameter("sha", sha).getResultList();
		return commits.isEmpty() ? null : commits.get(0);
	}

}
