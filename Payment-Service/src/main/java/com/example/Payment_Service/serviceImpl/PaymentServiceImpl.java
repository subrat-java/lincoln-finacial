package com.example.Payment_Service.serviceImpl;

import com.example.Payment_Service.client.PolicyClient;
import com.example.Payment_Service.dto.PaymentRequest;
import com.example.Payment_Service.dto.PaymentResponse;
import com.example.Payment_Service.dto.PolicyDto;
import com.example.Payment_Service.entity.Payment;
import com.example.Payment_Service.event.EventPublisher;
import com.example.Payment_Service.exception.BusinessException;
import com.example.Payment_Service.exception.ExternalServiceException;
import com.example.Payment_Service.exception.NotFoundException;
import com.example.Payment_Service.gateway.PaymentGateway;
import com.example.Payment_Service.repository.PaymentRepository;
import com.example.Payment_Service.service.PaymentService;
import com.example.Payment_Service.service.PolicyLookupService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
	private static final Set<String> ALLOWED_METHODS = Set.of("CARD", "UPI", "NET_BANKING");

	private final PaymentRepository repository;
	private final EventPublisher eventPublisher;

	private final PaymentGateway gateway;
	private final PolicyLookupService policyLookupService;

	public PaymentServiceImpl(
		PaymentRepository repository,
		PolicyClient policyClient,
		PaymentGateway gateway,
		PolicyLookupService policyLookupService,
		EventPublisher eventPublisher
	) {
		this.repository = repository;
		this.gateway = gateway;
		this.policyLookupService = policyLookupService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public PaymentResponse create(PaymentRequest request) {
		if (!ALLOWED_METHODS.contains(request.method().toUpperCase())) {
			throw new BusinessException("Unsupported payment method: " + request.method());
		}

		// 1. Verify policy exists (Feign, Resilience4j-wrapped)
		PolicyDto policy = policyLookupService.fetch(request.policyId());
		log.info("Payment for policy {} (customerId={})", policy.policyNumber(), policy.customerId());

		// 2. Save as PENDING
		Payment payment = new Payment();
		payment.setPolicyId(policy.id());
		payment.setCustomerId(policy.customerId());
		payment.setAmount(request.amount());
		payment.setMethod(request.method().toUpperCase());
		payment.setStatus("PENDING");
		payment = repository.save(payment);

		// 3. Call the gateway
		PaymentGateway.GatewayResult result = gateway.charge(policy.id(), request.amount(), payment.getMethod());

		// 4. Update status
		payment.setTxnRef(result.txnRef());
		if (result.success()) {
			payment.setStatus("SUCCESS");
		} else {
			payment.setStatus("FAILED");
			payment.setFailureReason(result.failureReason());
		}
		payment = repository.save(payment);

		if ("SUCCESS".equals(payment.getStatus())) {
			eventPublisher.publishPaymentSuccess(payment);
		} else {
			eventPublisher.publishPaymentFailed(payment);
		}

		log.info("Payment {} finished with status {}", payment.getId(), payment.getStatus());
		return PaymentResponse.from(payment);
	}

	@Override
	public PaymentResponse getById(Long id) {
		return repository
			.findById(id)
			.map(PaymentResponse::from)
			.orElseThrow(() -> new NotFoundException("Payment", id));
	}

	@Override
	public List<PaymentResponse> getByPolicy(Long policyId) {
		return repository.findByPolicyIdOrderByCreatedAtDesc(policyId).stream().map(PaymentResponse::from).toList();
	}

	@Override
	public List<PaymentResponse> getAll() {
		return repository.findAll().stream().map(PaymentResponse::from).toList();
	}

	@Override
	public PaymentResponse refund(Long id) {
		Payment payment = repository.findById(id).orElseThrow(() -> new NotFoundException("Payment", id));

		if (!"SUCCESS".equals(payment.getStatus())) {
			throw new BusinessException("Only SUCCESS payments can be refunded");
		}
		payment.setStatus("REFUNDED");
		Payment saved = repository.save(payment);
		eventPublisher.publishPaymentRefund(saved);
		return PaymentResponse.from(saved);
	}
}
