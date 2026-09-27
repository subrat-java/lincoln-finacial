package com.example.Audit_Service.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_entity",   columnList = "entity_type, entity_id"),
        @Index(name = "idx_event",    columnList = "event_type"),
        @Index(name = "idx_created",  columnList = "created_at")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;          // POLICY_CREATED, PAYMENT_SUCCESS, USER_LOGIN, ...

    @Column(name = "entity_type", length = 64)
    private String entityType;         // POLICY, PAYMENT, USER, CUSTOMER

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "actor_user_id")
    private Long actorUserId;          // X-User-Id from the gateway

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;            // raw JSON of the event

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public AuditLog() {}

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public Long getActorUserId() { return actorUserId; }
    public void setActorUserId(Long actorUserId) { this.actorUserId = actorUserId; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}