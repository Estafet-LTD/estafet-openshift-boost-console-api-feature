package com.estafet.openshift.boost.console.api.feature.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.feature.dao.FeatureDAO;
import com.estafet.openshift.boost.console.api.feature.model.BuildEnv;
import com.estafet.openshift.boost.console.api.feature.model.Feature;

public class FeatureService {

	@Autowired
	private FeatureDAO featureDAO;
	
	@Transactional
	public void updateBuildEnv(BuildEnv buildEnv) {
		
	}

	public boolean isIncompleteFeature(String repo, String sha) {
		Feature feature = featureDAO.getFeatureByCommit(repo, sha);
		return feature == null || !feature.getStatus().equals("DONE");
	}
	
}
