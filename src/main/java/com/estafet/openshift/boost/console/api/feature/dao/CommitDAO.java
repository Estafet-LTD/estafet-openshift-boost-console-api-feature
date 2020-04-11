package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.Collections;
import java.util.Comparator;
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
	
	public RepoCommit getMatched(String commitId) {
		return entityManager.find(RepoCommit.class, commitId);
	}
	
	public List<RepoCommit> getMatchedForMicroservice(String microservice) {
		TypedQuery<Matched> query = entityManager
				.createQuery("select m from Matched m where m.repo.microservice = ?1", Matched.class);
		return query.setParameter(1, microservice).getResultList();
	}
	
	public RepoCommit getCommit(String repo, String sha) {
		TypedQuery<RepoCommit> query = entityManager
				.createQuery("select c from RepoCommit c where c.repo.name = ?1 and c.sha = ?2", RepoCommit.class);
		List<RepoCommit> commits = query.setParameter(1, repo).setParameter(2, sha).getResultList();
		return commits.isEmpty() ? null : commits.get(0);
	}

}
