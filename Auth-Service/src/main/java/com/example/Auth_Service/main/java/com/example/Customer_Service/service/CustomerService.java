package com.example.Customer_Service.service;

import com.example.Customer_Service.entity.Customer;

import java.util.List;

public interface CustomerService {

    Customer getCustomerById(Long id);
    List<Customer> getAllCustomers();
    Customer saveCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);

}
