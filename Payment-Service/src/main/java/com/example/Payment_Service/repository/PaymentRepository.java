package com.example.Payment_Service.repository;

import com.example.Payment_Service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    List<Payment> findByPolicyIdOrderByCreatedAtDesc(Long policyId);

    List<Payment> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Payment> findByTxnRef(String txnRef);
}
