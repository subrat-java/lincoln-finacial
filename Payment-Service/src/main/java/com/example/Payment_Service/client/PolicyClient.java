package com.example.Payment_Service.client;

import com.example.Payment_Service.dto.PolicyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "POLICY-SERVICE")
public interface PolicyClient {

    @GetMapping("/api/policies/{id}")
    PolicyDto getPolicy(@PathVariable("id") Long id);
}