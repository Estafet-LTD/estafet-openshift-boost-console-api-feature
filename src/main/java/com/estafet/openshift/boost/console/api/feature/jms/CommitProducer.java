package com.estafet.openshift.boost.console.api.feature.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.estafet.openshift.boost.messages.features.CommitMessage;

@Component
public class CommitProducer {

	@Autowired 
	private JmsTemplate jmsTemplate;
	
	public void sendMessage(CommitMessage message) {
		jmsTemplate.setPubSubDomain(true);
		jmsTemplate.convertAndSend("commit.topic", message.toJSON());
	}
}
