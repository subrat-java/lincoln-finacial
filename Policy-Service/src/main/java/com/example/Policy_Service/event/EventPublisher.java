package com.example.Policy_Service.event;

import com.example.Policy_Service.entity.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);
    private static final String TOPIC = "policy-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final com.fasterxml.jackson.databind.ObjectMapper mapper;

    public EventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                          com.fasterxml.jackson.databind.ObjectMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Async
    public void publishPolicyCreated(Policy policy) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "POLICY_CREATED");
        event.put("policyId", policy.getId());
        event.put("policyNumber", policy.getPolicyNumber());
        event.put("customerId", policy.getCustomerId());
        event.put("timestamp", Instant.now().toString());
        send(event);
    }

    @Async
    public void publishPolicyUpdated(Policy policy) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "POLICY_UPDATED");
        event.put("policyId", policy.getId());
        event.put("policyNumber", policy.getPolicyNumber());
        event.put("customerId", policy.getCustomerId());
        event.put("timestamp", Instant.now().toString());
        send(event);
    }

    @Async
    public void publishPolicyDeleted(Long policyId) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "POLICY_DELETED");
        event.put("policyId", policyId);
        event.put("timestamp", Instant.now().toString());
        send(event);
    }

    private void send(Map<String, Object> event) {
        try {
            String json = mapper.writeValueAsString(event);
            String key = String.valueOf(event.get("policyId"));
            kafkaTemplate.send(TOPIC, key, json);
            log.info("Published to {}: {}", TOPIC, json);
        } catch (Exception ex) {
            log.error("Failed to publish event to {}", TOPIC, ex);
        }
    }
}