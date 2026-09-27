package com.example.Audit_Service.event;

import com.example.Audit_Service.entity.AuditLog;
import com.example.Audit_Service.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AuditLogWriter {

    private static final Logger log = LoggerFactory.getLogger(AuditLogWriter.class);

    private final AuditLogRepository repository;
    private final ObjectMapper mapper;

    public AuditLogWriter(AuditLogRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Async
    public void write(String entityType, String message) {
        try {
            JsonNode node = mapper.readTree(message);
            AuditLog audit = new AuditLog();
            audit.setEventType(node.path("eventType").asText());
            audit.setEntityType(entityType);
            audit.setEntityId(readId(node, entityType));
            audit.setPayload(message);
            repository.save(audit);
            log.info("Persisted audit_log id={} event={}", audit.getId(), audit.getEventType());
        } catch (Exception ex) {
            log.error("Failed to persist audit log for message={}", message, ex);
        }
    }

    private Long readId(JsonNode node, String entityType) {
        if ("POLICY".equals(entityType) && node.has("policyId")) {
            return node.get("policyId").asLong();
        }
        if ("PAYMENT".equals(entityType)) {
            if (node.has("paymentId")) return node.get("paymentId").asLong();
            if (node.has("policyId"))  return node.get("policyId").asLong();
        }
        return null;
    }
}