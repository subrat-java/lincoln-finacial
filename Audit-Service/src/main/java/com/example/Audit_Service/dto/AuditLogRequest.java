package com.example.Audit_Service.dto;

import jakarta.validation.constraints.NotBlank;

public record AuditLogRequest(
        @NotBlank String eventType,
        String entityType,
        Long entityId,
        Long actorUserId,
        String payload
) {}