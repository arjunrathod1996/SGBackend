package com.arni.phonenumber;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arni.Role.Role;
import com.arni.Role.RoleRepository;
import com.arni.customService.CustomUserDetailsService;
import com.arni.customer.Customer;
import com.arni.customer.CustomerRepository;
import com.arni.jwt.JWTService;
import com.arni.user.LoginMethod;
import com.arni.user.User;
import com.arni.user.UserRepository;
import com.arni.user.UsersVerification;
import com.arni.user.UsersVerificationRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/auth")
public class OTPController {
	
	 @Autowired
	 private UserRepository userRepository;
	 
	 @Autowired
     CustomUserDetailsService customerUserDetailService;
    
    @Autowired
    private UserDetailsService detailsService;
    
    @Autowired
    private JWTService jwtService;
    
    @Autowired
    UsersVerificationRepository usersVerificationRepository;
    
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    CustomerRepository customerRepository;
	
	 private static final Logger logger = LoggerFactory.getLogger(OTPController.class);
	 
	 @PostMapping("/generate-otp/{mobileNumber}")
	    public ResponseEntity<String> authenticateAndGetGeneratedOtp(@PathVariable String mobileNumber) {
	        // Generate a random 4-digit OTP
	        String otp = generateOTP();

	        // Retrieve the user by mobile number
	        User user = userRepository.findByPhoneNumber(mobileNumber);

	        // Retrieve the role for the customer
	        Role role = roleRepository.findByName(Role.RoleType.DEPARTMENT_ADMIN);


	        if (user == null) {
	            // Create a new user
	            user = new User();
	            user.setPhoneNumber(mobileNumber);
	            user.setRole(role);
	            userRepository.save(user); // Save the new user

	            // Check if a customer with the same mobile number already exists
	            Optional<Customer> existingCustomerOpt = customerRepository.findByMobileNumbers(mobileNumber);
	            
	            if (existingCustomerOpt.isEmpty()) {
	                // No existing customer found, create a new customer
	                Customer customer = new Customer();
	                customer.setMobileNumber(mobileNumber);
	                
	                customerRepository.save(customer);

	                // Link the customer to the user
	                user.setCustomer(customer);
	                user.setLoginMethod(LoginMethod.phoneNumber);
	            } else {
	                // Existing customer found, link it to the new user
	                user.setCustomer(existingCustomerOpt.get());
	            }
	            userRepository.save(user); // Update the user
	        }


	        // Check if the UsersVerification entry already exists for the given user
	        Optional<UsersVerification> existingVerification = usersVerificationRepository.findByContentAndContentType(mobileNumber, UsersVerification.ContentType.MOBILE_NUMBER);

	        if (existingVerification.isPresent()) {
	            // Update the existing verification
	            UsersVerification usersVerification = existingVerification.get();
	            usersVerification.setVerficationCode(otp);
	            usersVerification.setStatus(UsersVerification.Status.ACTIVE);
	            usersVerificationRepository.save(usersVerification);
	        } else {
	            // Create a new UsersVerification entry
	            UsersVerification newVerification = new UsersVerification();
	            newVerification.setContent(mobileNumber);
	            newVerification.setUser(user); // Set the user
	            newVerification.setVerficationCode(otp);
	            newVerification.setStatus(UsersVerification.Status.ACTIVE);
	            usersVerificationRepository.save(newVerification);
	        }

	        // Send the OTP in the response
	       // String responseMessage = "OTP generated successfully for mobile number: " + otp;
	        String responseMessage =  otp;
	        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	    }

	 
	 private String generateOTP() {
         Random random = new Random();
         int otp = 1000 + random.nextInt(9000); // Generate a random number between 1000 and 9999
         return String.valueOf(otp);
     }
	 
	 

	 
	 @PostMapping("/verify-otp/{mobileNumber}/{otp}")
	 public ResponseEntity<Map<String, String>> verifyOtp(@PathVariable String mobileNumber, @PathVariable String otp) {
	     UsersVerification usersVerification = usersVerificationRepository.findByContentAndVerficationCodeAndStatus(mobileNumber, otp, UsersVerification.Status.ACTIVE);

	     if (usersVerification != null) {
	         usersVerification.setStatus(UsersVerification.Status.CLOSED);
	         usersVerificationRepository.save(usersVerification);

	         User user = usersVerification.getUser();
	         if (user == null) {
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	         }

	         UserDetails userDetails = customerUserDetailService.loadUserByMobileAndOtp(user.getPhoneNumber());

	         String token = jwtService.generateToken(userDetails);

	         if (token != null) {
	             logger.info("JWT token generated successfully for user: {}", token);
	             // Return token in response body with key 'token'
	             Map<String, String> response = new HashMap<>();
	             response.put("token", token);
	             return ResponseEntity.ok(response);
	         } else {
	             logger.error("Failed to generate JWT token for user: {}", userDetails.getUsername());
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	         }
	     } else {
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	     }
	 }



	    private void setCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
	        Cookie cookie = new Cookie(name, value);
	        cookie.setMaxAge(maxAge);
	        cookie.setHttpOnly(httpOnly);
	        cookie.setPath("/"); // Ensure the path is set to root
	        response.addCookie(cookie);
	    }
	
}
