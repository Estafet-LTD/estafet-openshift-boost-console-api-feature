package com.estafet.boostcd.feature.api.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.estafet.boostcd.feature.api.service.FeatureService;
import com.estafet.openshift.boost.messages.features.FeatureMessage;

import io.opentracing.Tracer;

@Component
public class FeatureConsumer {

	public final static String TOPIC = "feature.topic";

	@Autowired
	private Tracer tracer;
	
	@Autowired
	private FeatureService featureService;

	@JmsListener(destination = TOPIC, containerFactory = "myFactory")
	public void onMessage(String message) {
		try {
			featureService.processFeature(FeatureMessage.fromJSON(message));
		} finally {
			if (tracer.activeSpan() != null) {
				tracer.activeSpan().close();
			}
		}
	}
	
}
