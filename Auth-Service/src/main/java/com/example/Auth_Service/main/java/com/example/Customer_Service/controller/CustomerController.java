package com.example.Customer_Service.controller;

import com.example.Customer_Service.entity.Customer;
import com.example.Customer_Service.service.CustomerService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerService service;

	public CustomerController(CustomerService service) {
		this.service = service;
	}

	@GetMapping
	public List<Customer> getAll() {
		return service.getAllCustomers();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Customer> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.getCustomerById(id));
	}

	@PostMapping
	public ResponseEntity<Customer> create(@RequestBody Customer customer) {
		return ResponseEntity.ok(service.saveCustomer(customer));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
		return ResponseEntity.ok(service.updateCustomer(id, customer));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.deleteCustomer(id);
		return ResponseEntity.noContent().build();
	}
}
