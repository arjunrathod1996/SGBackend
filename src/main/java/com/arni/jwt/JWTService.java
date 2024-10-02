package com.arni.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    // Replace with your generated base64 secret key
    private static final String SECRET_KEY_STRING = "3d5BbXt3rD4f1Dd0Gx1Q3L2n6Z4v4H1d5J3l2K6m8N9p0Q==";
    private static final SecretKey SECRET_KEY = new SecretKeySpec(Base64.getDecoder().decode(SECRET_KEY_STRING), SignatureAlgorithm.HS256.getJcaName());
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    // Extract username from token
    public String extractUsername(String token) {
        logger.debug("Extracting username from token.");
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        logger.debug("Extracting expiration date from token.");
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract claim from token using resolver function
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        logger.debug("Extracting claims from token.");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        logger.debug("Extracting all claims from token.");
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        logger.debug("Claims extracted: {}", claims);
        return claims;
    }

    // Generate token for user
    public String generateToken(UserDetails userDetails) {
        logger.debug("Generating token for user: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", "Stormpath");
        claims.put("name", "Micah Silverman");
        claims.put("scope", "admins");
        claims.put("sub", userDetails.getUsername());
        String token = createToken(claims, userDetails.getUsername());
        logger.debug("Token generated: {}", token);
        return token;
    }

    // Generate token for username
    public String generateToken(String username) {
        logger.debug("Generating token for username: {}", username);
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", "Stormpath");
        claims.put("name", "Micah Silverman");
        claims.put("scope", "admins");
        claims.put("sub", username);
        String token = createToken(claims, username);
        logger.debug("Token generated: {}", token);
        return token;
    }

    // Generate token for phone number and roles
    public String generateToken(String phoneNumber, List<String> roles) {
        logger.debug("Generating token for phone number: {} with roles: {}", phoneNumber, roles);
        String token = Jwts.builder()
                .setSubject(phoneNumber)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
        logger.debug("Token generated: {}", token);
        return token;
    }

    // Create token with claims, subject, issue time, expiration time, and sign with secret key
    private String createToken(Map<String, Object> claims, String subject) {
        logger.debug("Creating token with claims: {} and subject: {}", claims, subject);
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
        logger.debug("Token created: {}", token);
        return token;
    }

    // Check if token is expired
    private Boolean isTokenExpired(String token) {
        logger.debug("Checking if token is expired.");
        Boolean isExpired = extractExpiration(token).before(new Date());
        logger.debug("Token expired: {}", isExpired);
        return isExpired;
    }

    // Validate token by checking username and expiration
    public Boolean validateToken(String token, UserDetails userDetails) {
        logger.debug("Validating token for user: {}", userDetails.getUsername());
        final String username = extractUsername(token);
        Boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        logger.debug("Token is valid: {}", isValid);
        return isValid;
    }

    // Extract all details from token for logging or debugging
    public Map<String, Object> getTokenDetails(String token) {
        logger.debug("Extracting token details.");
        Claims claims = extractAllClaims(token);
        Map<String, Object> tokenDetails = new HashMap<>();
        tokenDetails.put("body", claims);
        tokenDetails.put("header", Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getHeader());
        tokenDetails.put("signature", token.split("\\.")[2]);
        tokenDetails.put("status", "SUCCESS");
        logger.debug("Token details extracted: {}", tokenDetails);
        return tokenDetails;
    }

    // Generate JWT token from Authentication object
    public String generateJwtToken(Authentication authentication) {
        logger.debug("Generating JWT token from Authentication object.");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = generateToken(userDetails);
        logger.debug("JWT token generated: {}", token);
        return token;
    }
}
