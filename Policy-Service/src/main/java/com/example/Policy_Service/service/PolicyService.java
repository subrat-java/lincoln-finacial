package com.example.Policy_Service.service;

import com.example.Policy_Service.dto.CustomerDto;
import com.example.Policy_Service.entity.Policy;

import java.util.List;

public interface PolicyService {

    Policy getPolicyById(Long id);
    List<Policy> getAllPolicies();
    Policy savePolicy(Policy policy);
    Policy updatePolicy(Long id,Policy policy);
    void deletePolicy(Long id);
    CustomerDto getCustomerForPolicy(Long policyId);

}
