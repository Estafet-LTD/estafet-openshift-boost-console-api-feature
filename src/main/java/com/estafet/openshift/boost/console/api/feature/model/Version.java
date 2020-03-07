package com.estafet.openshift.boost.console.api.feature.model;

import java.util.StringTokenizer;

public class Version {

	private final int major;
	private final int minor;
	private final int revision;

	private final boolean snapshot;

	public Version(String version) {
		if (version.endsWith("-SNAPSHOT")) {
			version = version.replaceAll("\\-SNAPSHOT", "");
			snapshot = true;
		} else {
			snapshot = false;
		}
		StringTokenizer tokenizer = new StringTokenizer(version, ".");
		major = Integer.parseInt(tokenizer.nextToken());
		minor = Integer.parseInt(tokenizer.nextToken());
		revision = Integer.parseInt(tokenizer.nextToken());
	}

	public boolean isSnapshot() {
		return snapshot;
	}

	public boolean isLessThanOrEqual(Version other) {
		if (major > other.major) {
			return false;
		} else if (minor > other.minor) {
			return false;
		} else if (revision > other.revision) {
			return false;
		}
		return true;
	}

}
