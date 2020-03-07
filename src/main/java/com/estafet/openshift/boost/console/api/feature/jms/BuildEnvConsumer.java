package com.estafet.openshift.boost.console.api.feature.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.estafet.openshift.boost.console.api.feature.message.BuildEnv;
import com.estafet.openshift.boost.console.api.feature.service.EnvironmentService;

import io.opentracing.Tracer;

@Component
public class BuildEnvConsumer {

	private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);
	
	public final static String TOPIC = "build.env.topic";

	@Autowired
	private Tracer tracer;
	
	@Autowired
	private EnvironmentService environmentService;

	@JmsListener(destination = TOPIC, containerFactory = "myFactory")
	public void onMessage(String message) {
		try {
			log.info("Received message - " + message);
			environmentService.processEnvUpdate(BuildEnv.fromJSON(message));
		} finally {
			if (tracer.activeSpan() != null) {
				tracer.activeSpan().close();
			}
		}
	}

}
