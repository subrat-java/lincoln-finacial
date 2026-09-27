package com.example.Policy_Service.serviceImpl;

import com.example.Policy_Service.client.CustomerClient;
import com.example.Policy_Service.dto.CustomerDto;
import com.example.Policy_Service.entity.Policy;
import com.example.Policy_Service.event.EventPublisher;
import com.example.Policy_Service.exception.ExternalServiceException;
import com.example.Policy_Service.exception.NotFoundException;
import com.example.Policy_Service.repository.PolicyRepository;
import com.example.Policy_Service.service.PolicyService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceImpl implements PolicyService {

	private static final Logger log = LoggerFactory.getLogger(PolicyServiceImpl.class);

	public PolicyRepository policyRepository;
	public final CustomerClient customerClient;
	private final EventPublisher eventPublisher;

	public PolicyServiceImpl(
		PolicyRepository policyRepository,
		CustomerClient customerClient,
		EventPublisher eventPublisher
	) {
		this.policyRepository = policyRepository;
		this.customerClient = customerClient;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public Policy getPolicyById(Long id) {
		return policyRepository.findById(id).orElseThrow(() -> new NotFoundException("PolicyNumber" + id));
	}

	@Override
	public List<Policy> getAllPolicies() {
		return policyRepository.findAll();
	}

	@Override
	public Policy savePolicy(Policy policy) {
		Policy saved = policyRepository.save(policy);
		eventPublisher.publishPolicyCreated(saved);
		return saved;
	}

	@Override
	public Policy updatePolicy(Long id, Policy policy) {
		Policy existing = getPolicyById(id);
		existing.setPolicyNumber(policy.getPolicyNumber());
		existing.setCustomerId(policy.getCustomerId());
		Policy saved = policyRepository.save(existing);
		eventPublisher.publishPolicyUpdated(existing);
		return saved;
	}

	@Override
	public void deletePolicy(Long id) {
		policyRepository.deleteById(id);
		eventPublisher.publishPolicyDeleted(id);
	}

	@Override
	@Retry(name = "customerService")
	@CircuitBreaker(name = "customerService", fallbackMethod = "getCustomerFallback")
	public CustomerDto getCustomerForPolicy(Long policyId) {
		Policy policy = getPolicyById(policyId);
		log.info("Feign call: policyId={} → customerId={}", policyId, policy.getCustomerId());
		return customerClient.getCustomer(policy.getCustomerId());
	}

	public CustomerDto getCustomerFallback(Long policyId, Throwable t) {
		log.warn(
			"Fallback triggered for policyId={} cause={}: {}",
			policyId,
			t.getClass().getSimpleName(),
			t.getMessage()
		);
		throw new ExternalServiceException("Customer service is currently unavailable. Please try again in a moment.");
	}
}
