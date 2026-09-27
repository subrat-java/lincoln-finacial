package com.example.Payment_Service.dto;

import com.example.Payment_Service.entity.Payment;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long policyId,
        Long customerId,
        BigDecimal amount,
        String method,
        String status,
        String txnRef,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(), p.getPolicyId(), p.getCustomerId(),
                p.getAmount(), p.getMethod(), p.getStatus(), p.getTxnRef(),
                p.getFailureReason(), p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}