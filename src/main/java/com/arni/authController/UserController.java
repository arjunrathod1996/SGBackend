package com.arni.authController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arni.user.User;
import com.arni.user.UsersManagementService;

//@RestController
//@RequestMapping("/user")
//public class UserController {
//
//    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
//
//    @Autowired
//    private UsersManagementService usersManagementService;
//
//    @GetMapping("/current")
//    public ResponseEntity<User> getCurrentUser() {
//    	
//        // Get the currently authenticated user
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserEmail = authentication.getName();
//        
//        logger.info("Fetching current user details for email: {}", currentUserEmail);
//
//        // Fetch user details using the email
//        User currentUser;
//        try {
//            currentUser = usersManagementService.getUserByEmail(currentUserEmail);
//            logger.info("Successfully fetched user details for email: {}", currentUserEmail);
//        } catch (Exception e) {
//            logger.error("Error fetching user details for email: {}", currentUserEmail, e);
//            return ResponseEntity.status(500).body(null);
//        }
//
//        // Return user details as the response
//        return ResponseEntity.ok(currentUser);
//    }
//}


@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UsersManagementService usersManagementService;

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser() {

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserIdentifier = authentication.getName(); // Could be email or phone number

        logger.info("Fetching current user details for identifier: {}", currentUserIdentifier);

        // Fetch user details using either email or phone number
        User currentUser;
        try {
            currentUser = usersManagementService.getUserByEmailOrPhoneNumber(currentUserIdentifier);
            logger.info("Successfully fetched user details for identifier: {}", currentUserIdentifier);
        } catch (Exception e) {
            logger.error("Error fetching user details for identifier: {}", currentUserIdentifier, e);
            return ResponseEntity.status(500).body(null);
        }

        // Return user details as the response
        return ResponseEntity.ok(currentUser);
    }
}
