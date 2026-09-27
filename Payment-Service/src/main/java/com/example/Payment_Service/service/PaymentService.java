package com.example.Payment_Service.service;

import com.example.Payment_Service.dto.PaymentRequest;
import com.example.Payment_Service.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse create(PaymentRequest request);
    PaymentResponse getById(Long id);
    List<PaymentResponse> getByPolicy(Long policyId);
    List<PaymentResponse> getAll();
    PaymentResponse refund(Long id);
}