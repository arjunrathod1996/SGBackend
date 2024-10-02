package com.arni.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
	
	 private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

	    @Autowired
	    private CustomerRepository customerRepository;
	    
	    public Customer createCustomer(Customer customer) throws Exception {
	        try {
	            logger.info("Creating customer: {}", customer);
	            // Business logic to handle customer creation
	            Customer createdCustomer = customerRepository.save(customer);
	            logger.info("Customer created with ID: {}", createdCustomer.getId());
	            return createdCustomer;
	        } catch (Exception e) {
	            logger.error("Error creating customer: {}", e.getMessage());
	            throw new Exception("Failed to create customer", e);
	        }
	    }

	
	    public Customer updateCustomer(Long id, Customer customer) throws Exception {
	        try {
	            logger.info("Updating customer with ID {}: {}", id, customer);
	            // Check if customer exists
	            if (!customerRepository.existsById(id)) {
	                throw new Exception("Customer not found with ID: " + id);
	            }
	            customer.setId(id);
	            // Business logic to handle customer update
	            Customer updatedCustomer = customerRepository.save(customer);
	            logger.info("Customer updated with ID: {}", updatedCustomer.getId());
	            return updatedCustomer;
	        } catch (Exception e) {
	            logger.error("Error updating customer with ID {}: {}", id, e.getMessage());
	            throw new Exception("Failed to update customer", e);
	        }
	    }

}
