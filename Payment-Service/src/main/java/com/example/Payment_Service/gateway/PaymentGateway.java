package com.example.Payment_Service.gateway;

import java.math.BigDecimal;

public interface PaymentGateway {

    /**
     * Simulates charging a card / UPI / net banking account.
     * Returns a GatewayResult with status and a transaction reference.
     */
    GatewayResult charge(Long policyId, BigDecimal amount, String method);

    record GatewayResult(boolean success, String txnRef, String failureReason) {}
}