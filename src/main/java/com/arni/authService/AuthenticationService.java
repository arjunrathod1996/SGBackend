package com.arni.authService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.arni.authRequestResponse.AuthenticationRequest;
import com.arni.authRequestResponse.AuthenticationResponse;
import com.arni.customService.CustomUserDetailsService;
import com.arni.jwt.JWTUtils;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JWTUtils jwtUtil;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Print email to the console for debugging purposes
        System.out.println("Authenticating user: " + request.getEmail());

        // Create an AuthenticationToken using the request's email and password
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Set the authentication context for the current thread
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Retrieve the UserDetails object from the authentication principal
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate a JWT token using the UserDetails object
      //  String token = jwtUtil.generateToken(userDetails);

        // Print the generated JWT token for debugging purposes
       // System.out.println("Generated JWT token: " + token);

        // Return the token encapsulated in an AuthenticationResponse object
        return new AuthenticationResponse(null);
    }
}
