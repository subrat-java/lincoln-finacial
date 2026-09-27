package com.example.Audit_Service.service;

import com.example.Audit_Service.dto.AuditLogRequest;
import com.example.Audit_Service.dto.AuditLogResponse;

import java.util.List;

public interface AuditLogService {

    AuditLogResponse log(AuditLogRequest request);
    List<AuditLogResponse> findAll();
    List<AuditLogResponse> findByEntity(String entityType, Long entityId);
    List<AuditLogResponse> findByEvent(String EventType);

}
