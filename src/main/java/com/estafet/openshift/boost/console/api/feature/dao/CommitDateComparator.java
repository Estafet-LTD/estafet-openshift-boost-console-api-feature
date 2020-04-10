package com.estafet.openshift.boost.console.api.feature.dao;

import java.util.Comparator;

import com.estafet.openshift.boost.console.api.feature.model.CommitDate;

public class CommitDateComparator implements Comparator<CommitDate> {

	@Override
	public int compare(CommitDate date1, CommitDate date2) {
		return date2.getDate().compareTo(date1.getDate());
	}

}
