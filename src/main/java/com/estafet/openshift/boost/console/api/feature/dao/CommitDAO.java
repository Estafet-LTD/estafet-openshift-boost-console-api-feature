package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.estafet.openshift.boost.console.api.feature.model.RepoCommit;

@Repository
public class CommitDAO {

	private static final Logger log = LoggerFactory.getLogger(CommitDAO.class);
	
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
				.createQuery("select c from RepoCommit c where c.repo.microservice = :microservice and c.feature is not null", RepoCommit.class);
		return query.setParameter("microservice", microservice).getResultList();
	}
	
	public RepoCommit getCommit(String repo, String sha) {
		log.info("repo - " + repo);
		log.info("sha - " + sha);
		TypedQuery<RepoCommit> query = entityManager
				.createQuery("select c from RepoCommit c where c.repo.name = :repo and c.sha = :sha", RepoCommit.class);
		List<RepoCommit> commits = query.setParameter("repo", repo).setParameter("sha", sha).getResultList();
		return commits.isEmpty() ? null : commits.get(0);
	}

	public boolean commitExists(RepoCommit repoCommit) {
		return getCommit(repoCommit.getRepo().getName(), repoCommit.getSha()) != null;
	}

}
