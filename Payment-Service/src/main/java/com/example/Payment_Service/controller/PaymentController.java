package com.example.Payment_Service.controller;

import com.example.Payment_Service.dto.PaymentRequest;
import com.example.Payment_Service.dto.PaymentResponse;
import com.example.Payment_Service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request){
        return ResponseEntity.ok(paymentService.create(request));
    }

    @GetMapping("/all")
    public List<PaymentResponse> getAll(){
        return paymentService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @GetMapping("/policy/{policyId}")
    public List<PaymentResponse> getByPolicy(@PathVariable Long policyId){
        return paymentService.getByPolicy(policyId);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse>refund(@PathVariable Long id){
        return ResponseEntity.ok(paymentService.refund(id));
    }
}
