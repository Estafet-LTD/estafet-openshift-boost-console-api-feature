package com.estafet.openshift.boost.console.api.feature.model;

import java.util.StringTokenizer;

public class Version {

	private int major;
	private int minor;
	private int revision;

	private final boolean snapshot;
	private String version;

	public Version(String version) {
		this.version = version;
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
		return compareMajor(other);
	}
	
	private boolean compareMajor(Version other) {
		if (major < other.major) {
			return true;
		} else if (major == other.major) {
			return compareMinor(other);
		}
		return false;
	}
	
	private boolean compareMinor(Version other) {
		if (minor < other.minor) {
			return true;
		} else if (minor == other.minor) {
			return compareRevision(other);
		}
		return false;
	}

	private boolean compareRevision(Version other) {
		return revision <= other.revision;
	}
	
	public Version increment() {
		revision++;
		return this;
	}

	public String toString() {
		return version;
	}

}
