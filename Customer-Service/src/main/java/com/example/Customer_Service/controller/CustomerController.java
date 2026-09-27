package com.example.Customer_Service.controller;

import com.example.Customer_Service.entity.Customer;
import com.example.Customer_Service.service.CustomerService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/customers"})
public class CustomerController {
   private final CustomerService service;

   public CustomerController(CustomerService service) {
      this.service = service;
   }

   @GetMapping
   public List<Customer> getAll() {
      return this.service.getAllCustomers();
   }

   @GetMapping({"/{id}"})
   public ResponseEntity<Customer> getById(@PathVariable Long id) {
      return ResponseEntity.ok(this.service.getCustomerById(id));
   }

   @PostMapping
   public ResponseEntity<Customer> create(@RequestBody Customer customer) {
      return ResponseEntity.ok(this.service.saveCustomer(customer));
   }

   @PutMapping({"/{id}"})
   public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
      return ResponseEntity.ok(this.service.updateCustomer(id, customer));
   }

   @DeleteMapping({"/{id}"})
   public ResponseEntity<Void> delete(@PathVariable Long id) {
      this.service.deleteCustomer(id);
      return ResponseEntity.noContent().build();
   }
}
