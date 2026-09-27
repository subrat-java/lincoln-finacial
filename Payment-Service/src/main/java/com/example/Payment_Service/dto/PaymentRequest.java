package com.example.Payment_Service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull Long policyId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String method   // CARD | UPI | NET_BANKING
) {}