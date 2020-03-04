package com.estafet.openshift.boost.console.api.feature.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.estafet.openshift.boost.console.api.feature.message.BuildEnv;
import com.estafet.openshift.boost.console.api.feature.service.EnvironmentService;
import com.estafet.openshift.boost.console.api.feature.service.FeatureService;
import com.estafet.openshift.boost.console.api.feature.service.RepoService;

import io.opentracing.Tracer;

@Component
public class BuildEnvConsumer {

	public final static String TOPIC = "buildenv.topic";

	@Autowired
	private Tracer tracer;
	
	@Autowired
	private RepoService repoService;
	
	@Autowired
	private EnvironmentService environmentService;

	@Autowired
	private FeatureService featureService;

	@JmsListener(destination = TOPIC, containerFactory = "myFactory")
	public void onMessage(String message) {
		try {
			BuildEnv buildEnv = BuildEnv.fromJSON(message);
			repoService.updateRepos(buildEnv);
			environmentService.updateEnv(buildEnv);
			featureService.updateFeatures(buildEnv);
		} finally {
			if (tracer.activeSpan() != null) {
				tracer.activeSpan().close();
			}
		}
	}

}
