package com.example.Policy_Service.controller;

import com.example.Policy_Service.dto.CustomerDto;
import com.example.Policy_Service.entity.Policy;
import com.example.Policy_Service.service.PolicyService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

	private final PolicyService policyService;

	public PolicyController(PolicyService policyService) {
		this.policyService = policyService;
	}

	@GetMapping
	public List<Policy> getPolicy() {
		return policyService.getAllPolicies();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Policy> getById(@PathVariable Long id) {
		return ResponseEntity.ok(policyService.getPolicyById(id));
	}

	@PostMapping
	public ResponseEntity<Policy> create(@RequestBody Policy policy) {
		return ResponseEntity.ok(policyService.savePolicy(policy));
	}

	@PutMapping
	public ResponseEntity<Policy> update(@PathVariable Long id, @RequestBody Policy policy) {
		return ResponseEntity.ok(policyService.updatePolicy(id, policy));
	}

	@DeleteMapping("/{id}/customer")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		policyService.deletePolicy(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/customer")
	public ResponseEntity<CustomerDto> getCustomerForPolicy(@PathVariable Long id) {
		return ResponseEntity.ok(policyService.getCustomerForPolicy(id));
	}
}
