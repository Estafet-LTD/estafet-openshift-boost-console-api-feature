package com.estafet.openshift.boost.console.api.feature.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.estafet.openshift.boost.console.api.feature.message.EnvFeatureMessage;

@Component
public class NewEnvironmentFeatureProducer {

	@Autowired 
	private JmsTemplate jmsTemplate;
	
	public void sendMessage(EnvFeatureMessage message) {
		jmsTemplate.setPubSubDomain(true);
		jmsTemplate.convertAndSend("new.environment.feature.topic", message.toJSON());
	}
	
}
