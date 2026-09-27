package com.example.Audit_Service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

	private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

	private final AuditLogWriter writer;

	public EventConsumer(AuditLogWriter writer) {
		this.writer = writer;
	}

	@KafkaListener(topics = "policy-events", groupId = "audit-service")
	public void consumePolicyEvent(String message) {
		log.info("Received policy-event: {}", message);
		writer.write("POLICY", message);
	}

	@KafkaListener(topics = "payment-events", groupId = "audit-service")
	public void consumePaymentEvent(String message) {
		log.info("Received payment-event: {}", message);
		writer.write("PAYMENT", message);
	}
}
