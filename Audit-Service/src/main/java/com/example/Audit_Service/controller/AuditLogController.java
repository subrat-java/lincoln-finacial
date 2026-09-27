package com.example.Audit_Service.controller;

import com.example.Audit_Service.dto.AuditLogRequest;
import com.example.Audit_Service.dto.AuditLogResponse;
import com.example.Audit_Service.service.AuditLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }


    @PostMapping("/logs")
    public ResponseEntity<AuditLogResponse> log(@Valid @RequestBody AuditLogRequest request){
        return ResponseEntity.ok(auditLogService.log(request));
    }

    @GetMapping("/all")
    public List<AuditLogResponse> findAll(){
        return auditLogService.findAll();
    }

    @GetMapping("/logs/entity")
    public List<AuditLogResponse> byEntity(@RequestParam String entityType, @RequestParam Long entityId){
        return  auditLogService.findByEntity(entityType,entityId);
    }

    @GetMapping("/logs/event")
    public List<AuditLogResponse> byEvent(@RequestParam String eventType){
        return auditLogService.findByEvent(eventType);
    }
}
