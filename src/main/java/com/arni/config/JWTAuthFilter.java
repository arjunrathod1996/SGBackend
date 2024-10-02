package com.arni.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import com.arni.authController.AuthController;
import com.arni.customService.CustomUserDetailsService;
import com.arni.jwt.JWTService;
import com.arni.jwt.JWTUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthFilter.class);

    private final JWTService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JWTAuthFilter(JWTService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwtToken = null;
        try {
            jwtToken = extractTokenFromRequest(request);
            if (jwtToken != null) {
                logger.debug("JWT token extracted from request: {}", jwtToken);

                String username = null;
                try {
                    username = jwtService.extractUsername(jwtToken);
                    logger.info("Username extracted from JWT: {}", username);
                } catch (Exception e) {
                    logger.error("Error extracting username from JWT: {}", e.getMessage());
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails;
                    
                    // Check if username is an email or phone number
                    try {
                        if (isEmail(username)) {
                            userDetails = customUserDetailsService.loadUserByUsername(username);
                            logger.info("User details loaded for email: {}", username);
                        } else {
                            userDetails = customUserDetailsService.loadUserByMobileAndOtp(username); // OTP validation is already done at this point
                            logger.info("User details loaded for phone number: {}", username);
                        }
                    } catch (UsernameNotFoundException e) {
                        logger.error("User not found: {}", username);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    logger.debug("User details: {}", userDetails);

                    if (jwtService.validateToken(jwtToken, userDetails)) {
                        logger.info("JWT token is valid. Setting authentication.");

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                        logger.info("Authentication set in SecurityContext.");
                    } else {
                        logger.warn("JWT token is not valid.");
                    }
                } else {
                    logger.debug("No valid JWT token found or user already authenticated.");
                }
            } else {
                logger.debug("No JWT token found in request.");
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isEmail(String username) {
        boolean isEmail = username.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        logger.debug("Username '{}' is email: {}", username, isEmail);
        return isEmail;
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = null;
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            logger.debug("Token extracted from Authorization header: {}", token);
        } else if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    logger.debug("Token extracted from cookies: {}", token);
                    break;
                }
            }
        }
        if (token == null) {
            logger.debug("No token found in request headers or cookies.");
        }
        return token;
    }
}
