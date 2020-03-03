package com.estafet.openshift.boost.console.api.feature.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("UNMATCHED")
public class Unmatched extends RepoCommit {

	
	
}
