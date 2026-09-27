package com.example.Customer_Service.serviceImpl;

import com.example.Customer_Service.entity.Customer;
import com.example.Customer_Service.exception.NotFoundException;
import com.example.Customer_Service.repository.CustomerRepository;
import com.example.Customer_Service.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // <--- This is what tells Spring to create the bean!
public class CustomerServiceImpl implements CustomerService {

   private final CustomerRepository customerRepository;

   // Constructor Injection
   public CustomerServiceImpl(CustomerRepository customerRepository) {
      this.customerRepository = customerRepository;
   }

   @Override
   public Customer getCustomerById(Long id) {
      // If not found, throw an exception (or you can return null)
      return customerRepository.findById(id)
              .orElseThrow(() -> new NotFoundException("Customer" + id));
   }

   @Override
   public List<Customer> getAllCustomers() {
      return customerRepository.findAll();
   }

   @Override
   public Customer saveCustomer(Customer customer) {
      return customerRepository.save(customer);
   }

   @Override
   public Customer updateCustomer(Long id, Customer customer) {
      Customer existingCustomer = getCustomerById(id);
      existingCustomer.setName(customer.getName());
      existingCustomer.setEmail(customer.getEmail());
      return customerRepository.save(existingCustomer);
   }

   @Override
   public void deleteCustomer(Long id) {
      customerRepository.deleteById(id);
   }
}