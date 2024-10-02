package com.arni.authController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arni.authRequestResponse.AuthenticationRequest;
import com.arni.authRequestResponse.AuthenticationResponse;
import com.arni.dto.LoginRequest;
import com.arni.jwt.JWTService;
import com.arni.jwt.JWTUtils;
import com.arni.response.JwtResponse;
import com.arni.service.CommonService;
import com.arni.user.ReqRes;
import com.arni.user.User;
import com.arni.user.UserRepository;
import com.arni.user.UsersManagementService;

@RestController
@RequestMapping("/login")
public class AuthController {
	
	public Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtUtils;
    
    @Autowired
    CommonService commonService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Received authentication request for email: {}", loginRequest.getEmail());

        try {
            // Perform authentication
            logger.info("Attempting to authenticate user with email: {}", loginRequest.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                )
            );
            logger.info("User authenticated successfully: {}", loginRequest.getEmail());

            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Authentication set in SecurityContext for user: {}", loginRequest.getEmail());

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);
            logger.info("JWT token generated for user: {}", loginRequest.getEmail());

            // Return successful response with JWT token
            logger.info("Returning successful authentication response for user: {}", loginRequest.getEmail());
            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (Exception e) {
            // Log the error and return unauthorized response
            logger.error("Authentication failed for email: {} with error: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}
