package com.estafet.openshift.boost.console.api.feature.dto;

import java.util.Comparator;

public class FeatureDTOComparator implements Comparator<FeatureDTO> {

	@Override
	public int compare(FeatureDTO feature1, FeatureDTO feature2) {
		if (feature1.getWaitingSince() == null && feature2.getWaitingSince() == null) {
			return feature1.getFeatureId().compareTo(feature2.getFeatureId());
		} else if (feature1.getWaitingSince() != null && feature2.getWaitingSince() == null) {
			return 1;
		} else if (feature2.getWaitingSince() != null && feature1.getWaitingSince() == null) {
			return -1;
		}
		return feature2.waitingSinceDate().compareTo(feature1.waitingSinceDate());
	}

}
