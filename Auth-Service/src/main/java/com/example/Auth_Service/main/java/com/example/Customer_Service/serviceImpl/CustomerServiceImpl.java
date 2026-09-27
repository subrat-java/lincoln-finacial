package com.example.Customer_Service.serviceImpl;

import com.example.Customer_Service.entity.Customer;
import com.example.Customer_Service.repository.CustomerRepository;
import com.example.Customer_Service.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository repository;

	public CustomerServiceImpl(CustomerRepository repository) {
		this.repository = repository;
	}

	@Override
	public Customer getCustomerById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found:" + id));
	}

	@Override
	public List<Customer> getAllCustomers() {
		return repository.findAll();
	}

	@Override
	public Customer saveCustomer(Customer customer) {
		return repository.save(customer);
	}

	@Override
	public Customer updateCustomer(Long id, Customer customer) {
		Customer existing = getCustomerById(id);
		existing.setName(customer.getName());
		existing.setEmail(customer.getEmail());
		return repository.save(existing);
	}

	@Override
	public void deleteCustomer(Long id) {
		repository.deleteById(id);
	}
}
