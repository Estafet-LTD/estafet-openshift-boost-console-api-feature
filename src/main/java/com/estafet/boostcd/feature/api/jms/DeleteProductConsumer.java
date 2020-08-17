package com.estafet.boostcd.feature.api.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.estafet.boostcd.feature.api.service.EnvironmentService;
import com.estafet.openshift.boost.messages.environments.Environments;

import io.opentracing.Tracer;

@Component
public class DeleteProductConsumer {

	public static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);
	
	public final static String TOPIC = "delete.environments.topic";

	@Autowired
	private Tracer tracer;
	
	@Autowired
	private EnvironmentService environmentService;

	@JmsListener(destination = TOPIC, containerFactory = "myFactory")
	public void onMessage(String message) {
		try {
			log.info("Received message - " + message);
			environmentService.deleteEnvironments(Environments.fromJSON(message));
		} finally {
			if (tracer.activeSpan() != null) {
				tracer.activeSpan().close();
			}
		}
	}

}
