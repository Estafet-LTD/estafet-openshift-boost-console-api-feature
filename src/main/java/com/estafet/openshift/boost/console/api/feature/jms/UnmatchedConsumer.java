package com.estafet.openshift.boost.console.api.feature.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.estafet.openshift.boost.console.api.feature.service.CommitService;
import com.estafet.openshift.boost.messages.model.UnmatchedCommitMessage;

import io.opentracing.Tracer;

@Component
public class UnmatchedConsumer {

	public final static String TOPIC = "unmatched.commit.topic";

	@Autowired
	private Tracer tracer;

	@Autowired
	private CommitService commitService;

	@JmsListener(destination = TOPIC, containerFactory = "myFactory")
	public void onMessage(String message) {
		try {
			UnmatchedCommitMessage unmatchedCommitMessage = UnmatchedCommitMessage.fromJSON(message);
			commitService.processUnmatched(unmatchedCommitMessage);
		} finally {
			if (tracer.activeSpan() != null) {
				tracer.activeSpan().close();
			}
		}
	}

}
