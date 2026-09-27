package com.example.Payment_Service.service;

import com.example.Payment_Service.client.PolicyClient;
import com.example.Payment_Service.dto.PolicyDto;
import com.example.Payment_Service.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PolicyLookupService {

    private static final Logger log = LoggerFactory.getLogger(PolicyLookupService.class);
    private final PolicyClient policyClient;

    public PolicyLookupService(PolicyClient policyClient) {
        this.policyClient = policyClient;
    }

    @Retry(name = "policyService")
    @CircuitBreaker(name = "policyService", fallbackMethod = "fallback")
    public PolicyDto fetch(Long policyId) {
        return policyClient.getPolicy(policyId);
    }

    public PolicyDto fallback(Long policyId, Throwable t) {
        log.warn("Policy fallback policyId={} cause={}: {}",
                policyId, t.getClass().getSimpleName(), t.getMessage());
        throw new ExternalServiceException(
                "Policy service is currently unavailable. Please try again in a moment.");
    }
}