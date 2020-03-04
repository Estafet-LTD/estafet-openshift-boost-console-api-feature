package com.estafet.openshift.boost.console.api.feature.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepoUtils {

	private static final Logger log = LoggerFactory.getLogger(RepoUtils.class);
	
	public static String getRepoFromURL(String github, String repoUrl) {
		log.info("repoURL - " + repoUrl);
		String githubUri = Pattern.quote("https://github.com/");
		String githubOrg = Pattern.quote(github + "/");
		Pattern r = Pattern.compile("(" + githubUri + ")(" + githubOrg + ")(.+)");
		log.info("pattern - " + r.pattern());
		Matcher m = r.matcher(repoUrl); 
		m.find();
		String repo = m.group(3);
		return repo;
	}

}
