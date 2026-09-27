package com.example.Customer_Service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(
   name = "customers"
)
public class Customer {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   public Long id;
   @Column(
      nullable = false
   )
   public String name;
   @Column(
      nullable = false,
      unique = true
   )
   public String email;

   public Long getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getEmail() {
      return this.email;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Customer(String name, String email) {
      this.name = name;
      this.email = email;
   }

   public Customer() {
   }
}
