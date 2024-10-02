package com.arni.user;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arni.Role.RoleRepository;
import com.arni.authController.AuthController;
import com.arni.jwt.JWTUtils;

//@Service
//public class UsersManagementService {
//	
//		public Logger logger = LoggerFactory.getLogger(AuthController.class);
//
//		 @Autowired
//		    private UserRepository userRepository;
//
//		    public User getUserByEmail(String email) {
//		        return userRepository.findByEmail(email)
//		                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
//		    }
//
//	}


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsersManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UsersManagementService.class);

    @Autowired
    private UserRepository userRepository;

    // Get user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // Get user by phone number
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    // Get user by either email or phone number
    public User getUserByEmailOrPhoneNumber(String identifier) {
        // First try to find the user by email
        Optional<User> user = userRepository.findByEmail(identifier);
        if (user.isPresent()) {
            return user.get();
        }

        // If not found by email, try to find the user by phone number
        user = Optional.ofNullable(userRepository.findByPhoneNumber(identifier));
        if (user.isPresent()) {
            return user.get();
        }

        // If not found by either, throw an exception
        throw new RuntimeException("User not found with email or phone number: " + identifier);
    }
}
