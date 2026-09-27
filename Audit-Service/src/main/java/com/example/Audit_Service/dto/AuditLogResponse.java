package com.example.Audit_Service.dto;

import com.example.Audit_Service.entity.AuditLog;

import java.time.Instant;

public record AuditLogResponse(
        Long id,
        String eventType,
        String entityType,
        Long entityId,
        Long actorUserId,
        String payload,
        Instant createdAt
) {
    public static AuditLogResponse from(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getEventType(),
                log.getEntityType(),
                log.getEntityId(),
                log.getActorUserId(),
                log.getPayload(),
                log.getCreatedAt()
        );
    }
}