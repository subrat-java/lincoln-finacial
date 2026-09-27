package com.example.Audit_Service.serviceImpl;

import com.example.Audit_Service.dto.AuditLogRequest;
import com.example.Audit_Service.dto.AuditLogResponse;
import com.example.Audit_Service.entity.AuditLog;
import com.example.Audit_Service.repository.AuditLogRepository;
import com.example.Audit_Service.service.AuditLogService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLogResponse log(AuditLogRequest request) {
        AuditLog entity = new AuditLog();
        entity.setEventType(request.eventType());
        entity.setEntityType(request.entityType());
        entity.setEntityId(request.entityId());
        entity.setActorUserId(request.actorUserId());
        entity.setPayload(request.payload());


        return AuditLogResponse.from(auditLogRepository.save(entity));
    }

    @Override
    public List<AuditLogResponse> findAll() {

        return auditLogRepository.findAll().stream().map(AuditLogResponse::from).toList();
    }

    @Override
    public List<AuditLogResponse> findByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType,entityId)
                .stream()
                .map(AuditLogResponse::from).toList();
    }

    @Override
    public List<AuditLogResponse> findByEvent(String EventType) {
        return auditLogRepository.findByEventTypeOrderByCreatedAtDesc(EventType)
                .stream()
                .map(AuditLogResponse::from)
                .toList();
    }
}
