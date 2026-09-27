package com.example.Payment_Service.event;

import com.example.Payment_Service.entity.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.sax.SAXResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventPublisher {

	private static final String TOPIC = "payment-events";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper mapper;

	public EventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper mapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.mapper = mapper;
	}

	@Async
	public void publishPaymentSuccess(Payment payment) {
		Map<String, Object> event = new HashMap<>();
		event.put("eventType", "PAYMENT_SUCCESS");
		event.put("paymentId", payment.getId());
		event.put("policyId", payment.getPolicyId());
		event.put("customerId", payment.getCustomerId());
		event.put("amount", payment.getAmount());
		event.put("method", payment.getMethod());
		event.put("txnRef", payment.getTxnRef());
		event.put("timestamp", Instant.now().toString());
		send(event);
	}

	@Async
	public void publishPaymentFailed(Payment payment) {
		Map<String, Object> event = new HashMap<>();
		event.put("eventType", "PAYMENT_FAILED");
		event.put("paymentId", payment.getId());
		event.put("policyId", payment.getPolicyId());
		event.put("customerId", payment.getCustomerId());
		event.put("amount", payment.getAmount());
		event.put("method", payment.getMethod());
		event.put("reason", payment.getFailureReason());
		event.put("timestamp", Instant.now().toString());
		send(event);
	}

	@Async
	public void publishPaymentRefund(Payment payment) {
		Map<String, Object> event = new HashMap<>();
		event.put("eventType", "PAYMENT_REFUNDED");
		event.put("paymentId", payment.getId());
		event.put("policyId", payment.getPolicyId());
		event.put("amount", payment.getAmount());
		event.put("timestamp", Instant.now().toString());
		send(event);
	}

	public void send(Map<String, Object> event) {
		try {
			String json = mapper.writeValueAsString(event);
			String key = String.valueOf(event.get("policyId"));
			kafkaTemplate.send(TOPIC, key, json);
			log.info("Published to {}: {}", TOPIC, json);
		} catch (Exception e) {
			log.error("Failed to publish event to {}", TOPIC, e);
		}
	}
}
