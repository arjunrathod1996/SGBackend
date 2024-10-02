package com.arni.customer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arni.geolocation.Country;
import com.arni.geolocation.CountryRepository;
import com.arni.geolocation.Region;
import com.arni.geolocation.RegionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private RegionRepository regionRepository;

    @PostMapping("/customers")
    public ResponseEntity<?> createCustomer(
        @RequestBody Customer customer,
        @RequestParam(value = "countryID", required = false) Long countryID,
        @RequestParam(value = "regionID", required = false) Long regionID
    ) {
        try {
            // Logging for debugging
            logger.info("Creating new customer: {}", customer);
            
//            // Optionally, you can set country and region if they are provided
//            if (countryID != null) {
//                // Assuming a method to find country by ID exists
//                Country country = countryRepository.findById(countryID).get();
//                if (country != null) {
//                    customer.setCountry(country);
//                }
//            }
//            
//            if (regionID != null) {
//                // Assuming a method to find region by ID exists
//                Region region = regionRepository.findById(regionID).get();
//                if (region != null) {
//                    customer.setRegion(region);
//                }
//            }
//            
//            // Create the customer
//            Customer createdCustomer = customerService.createCustomer(customer);
            
            // Return a response with the created customer
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the error and return an internal server error response
            logger.error("Error creating customer: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/customers/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        try {
            logger.info("Updating customer with ID {}: {}", id, customer);
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating customer with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

