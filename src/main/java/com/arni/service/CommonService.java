package com.arni.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arni.user.User;
import com.arni.user.UserRepository;



@Service
public class CommonService {
	
	@Autowired
	UserRepository userRepository;
	
	public static float LOW_BALANCE = 100f;
	
	public static Float MIN_BALANCE_REQUIRED = 100.0f;
	
	public static final String DEFAULT_COUNTRY_CALLING_CODE = "91";
	
	public static final float MIN_BALANCE = 5.0f;
	
	private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

	public User getLoggedInUser() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.isAuthenticated()) {
	        String loggedInUsername = authentication.getName();
	         
	        try {
	            // Attempt to retrieve the user by email
	            Optional<User> userByEmailOptional = userRepository.findByEmail(loggedInUsername);
	            if (userByEmailOptional.isPresent()) {
	                User userByEmail = userByEmailOptional.get();
	                logger.info("Logged-in user retrieved successfully by email: {}", loggedInUsername);
	                return userByEmail;
	            } else {
	                logger.info("No user found with email: {}", loggedInUsername);
	            }
	             
	            // Attempt to retrieve the user by phone number
	            Optional<User> userByPhoneNumberOptional = Optional.ofNullable(userRepository.findByPhoneNumber(loggedInUsername));
	            if (userByPhoneNumberOptional.isPresent()) {
	                User userByPhoneNumber = userByPhoneNumberOptional.get();
	                logger.info("Logged-in user retrieved successfully by phone number: {}", loggedInUsername);
	                return userByPhoneNumber;
	            } else {
	                logger.info("No user found with phone number: {}", loggedInUsername);
	            }
	            
	            logger.error("Logged-in user not found in the database: {}", loggedInUsername);
	        } catch (Exception e) {
	            logger.error("Error occurred while retrieving logged-in user: {}", e.getMessage());
	        }
	    } else {
	        logger.error("No authenticated user found.");
	    }
	    return null;
	}
}

