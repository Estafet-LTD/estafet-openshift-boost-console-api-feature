package com.estafet.openshift.boost.console.api.feature.dto;

import java.text.ParseException;
import java.util.Comparator;

import com.estafet.openshift.boost.commons.lib.date.DateUtils;

public class FeatureDTOComparator implements Comparator<FeatureDTO> {

	@Override
	public int compare(FeatureDTO feature1, FeatureDTO feature2) {
		try {
			if (feature1.getWaitingSince() == null && feature2.getWaitingSince() == null) {
				return feature1.getFeatureId().compareTo(feature2.getFeatureId());
			} else if (feature1.getWaitingSince() != null && feature2.getWaitingSince() == null) {
				return -1;
			} else if (feature2.getWaitingSince() != null && feature1.getWaitingSince() == null) {
				return 1;
			}
			return DateUtils.dateFormat.parse(feature2.getWaitingSince())
					.compareTo(DateUtils.dateFormat.parse(feature1.getWaitingSince()));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
