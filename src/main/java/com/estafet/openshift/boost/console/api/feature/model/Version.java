package com.estafet.openshift.boost.console.api.feature.model;

import java.util.StringTokenizer;

public class Version {

	private final int major;
	private final int minor;
	private final int revision;

	public Version(String version) {
		StringTokenizer tokenizer = new StringTokenizer(version, ".");
		major = Integer.parseInt(tokenizer.nextToken());
		minor = Integer.parseInt(tokenizer.nextToken());
		revision = Integer.parseInt(tokenizer.nextToken());
	}

	public boolean isLessThanOrEqual(Version other) {
		if (other.major > major) {
			return false;
		} else if (other.minor > minor) {
			return false;
		} else if (other.revision > revision) {
			return false;
		}
		return true;
	}

}
