package com.estafet.openshift.boost.console.api.feature.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.RepoCommit;

@Repository
public class CommitDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public RepoCommit getUnmatched(String repo, String sha) {
		TypedQuery<RepoCommit> query = entityManager
				.createQuery("select u from RepoCommit u where u.repo.name = ?1 and u.commitId = ?2", RepoCommit.class);
		return query.setParameter(1, repo).setParameter(2, sha).getSingleResult();
	}

}
