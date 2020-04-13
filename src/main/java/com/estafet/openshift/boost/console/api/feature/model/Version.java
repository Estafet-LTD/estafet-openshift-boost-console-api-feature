package com.estafet.openshift.boost.console.api.feature.model;

import java.util.StringTokenizer;

public class Version {

	private final int major;
	private final int minor;
	private int revision;
	private final boolean snapshot;

	public Version(String version) {
		if (version.endsWith("-SNAPSHOT")) {
			version = version.replaceAll("\\-SNAPSHOT", "");
			snapshot = true;
		} else {
			snapshot = false;
		}
		version = replaceVersionCharacter(version);
		StringTokenizer tokenizer = new StringTokenizer(version, ".");
		major = Integer.parseInt(tokenizer.nextToken());
		minor = Integer.parseInt(tokenizer.nextToken());
		revision = Integer.parseInt(tokenizer.nextToken());
	}

	private String replaceVersionCharacter(String version) {
		if(version.contains("v")){
			version = version.replace("v", "");
		}
		return version;
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
		StringBuilder builder = new StringBuilder();
		builder.append(major);
		builder.append(".");
		builder.append(minor);
		builder.append(".");
		builder.append(revision);
		if (snapshot) {
			builder.append("-SNAPSHOT");
		}
		return builder.toString();
	}

}
