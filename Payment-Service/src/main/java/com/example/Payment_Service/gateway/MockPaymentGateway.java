package com.example.Payment_Service.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MockPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentGateway.class);

    @Override
    public GatewayResult charge(Long policyId, BigDecimal amount, String method) {
        // Simulate network latency to the "provider"
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int roll = ThreadLocalRandom.current().nextInt(100);
        String txnRef = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        if (roll < 90) {   // 90% success
            log.info("MockGateway SUCCESS policyId={} amount={} method={} txnRef={}",
                    policyId, amount, method, txnRef);
            return new GatewayResult(true, txnRef, null);
        } else {           // 10% failure
            String reason = switch (roll) {
                case 90, 91 -> "Insufficient funds";
                case 92, 93 -> "Card declined by issuer";
                case 94     -> "Network timeout";
                case 95     -> "Invalid CVV";
                default     -> "Payment provider error";
            };
            log.warn("MockGateway FAILED policyId={} reason={}", policyId, reason);
            return new GatewayResult(false, txnRef, reason);
        }
    }
}