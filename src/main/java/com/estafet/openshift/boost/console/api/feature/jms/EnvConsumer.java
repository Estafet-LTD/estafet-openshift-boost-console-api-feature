package com.estafet.openshift.boost.console.api.feature.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.estafet.openshift.boost.console.api.feature.service.EnvironmentService;
import com.estafet.openshift.boost.console.api.feature.service.FeatureService;
import com.estafet.openshift.boost.console.api.feature.service.RepositoryService;
import com.estafet.openshift.boost.messages.environments.Environment;

import io.opentracing.Tracer;

@Component
public class EnvConsumer {

	private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);
	
	public final static String TOPIC = "env.topic";

	@Autowired
	private Tracer tracer;
	
	@Autowired
	private EnvironmentService environmentService;
	
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private FeatureService featureService;

	@JmsListener(destination = TOPIC, containerFactory = "myFactory")
	public void onMessage(String message) {
		try {
			log.info("Received message - " + message);
			Environment envMessage = Environment.fromJSON(message);
			log.info("env - " + envMessage.getName());
			if (environmentService.createEnv(envMessage)) {
				repositoryService.updateRepos(envMessage);
				environmentService.updateMicroservices(envMessage);
				featureService.updateEnvFeatures(envMessage);
			}
		} finally {
			if (tracer.activeSpan() != null) {
				tracer.activeSpan().close();
			}
		}
	}

}
