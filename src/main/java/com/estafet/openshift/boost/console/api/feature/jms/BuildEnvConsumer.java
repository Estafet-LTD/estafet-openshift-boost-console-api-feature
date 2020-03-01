package com.estafet.openshift.boost.console.api.feature.jms;

import com.estafet.openshift.boost.console.api.feature.model.BuildEnv;
import com.estafet.openshift.boost.console.api.feature.service.FeatureService;
import com.estafet.openshift.boost.console.api.feature.service.RepoService;

import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class BuildEnvConsumer {

	public final static String TOPIC = "buildenv-topic";

	@Autowired
	private Tracer tracer;
	
	@Autowired
	private RepoService repoService;
	
	@Autowired
	private FeatureService featureService;

	@JmsListener(destination = TOPIC, containerFactory = "myFactory")
	public void onMessage(String message) {
		try {
			BuildEnv buildEnv = BuildEnv.fromJSON(message);
			repoService.updateRepo(buildEnv);
			featureService.updateBuildEnv(buildEnv);
		} finally {
			if (tracer.activeSpan() != null) {
				tracer.activeSpan().close();
			}
		}
	}

}
