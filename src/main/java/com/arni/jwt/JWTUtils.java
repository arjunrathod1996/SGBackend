package com.arni.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.arni.authController.AuthController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JWTUtils {
	
	public Logger logger = LoggerFactory.getLogger(JWTUtils.class);

	 private String secretKey = "3d5BbXt3rD4f1Dd0Gx1Q3L2n6Z4v4H1d5J3l2K6m8N9p0Q==";

	 public String generateToken(String username) {
		    String token = Jwts.builder()
		            .setSubject(username)
		            .setIssuedAt(new Date())
		            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiry
		            .signWith(SignatureAlgorithm.HS256, secretKey)
		            .compact();
		    logger.info("Generated JWT token: {}", token); // Log the token for debugging
		    return token;
		}



//    public Claims extractClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(secretKey)
//                .parseClaimsJws(token)
//                .getBody();
//    }
    
//    public Claims extractClaims(String token) {
//        try {
//            return Jwts.parser()
//                .setSigningKey(secretKey)
//                .parseClaimsJws(token)
//                .getBody();
//        } catch (ExpiredJwtException e) {
//            // Handle the expired token scenario
//            throw new RuntimeException("JWT expired", e);
//        }
//    }
	 
	 
	 public Claims extractClaims(String token) {
		    try {
		        return Jwts.parser()
		            .setSigningKey(secretKey)
		            .parseClaimsJws(token)
		            .getBody();
		    } catch (ExpiredJwtException e) {
		        throw new RuntimeException("JWT expired", e);
		    } catch (SignatureException e) {
		        throw new RuntimeException("JWT signature invalid", e);
		    } catch (Exception e) {
		        throw new RuntimeException("JWT parsing error", e);
		    }
		}

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

//    public boolean isTokenExpired(String token) {
//        return extractClaims(token).getExpiration().before(new Date());
//    }

    public boolean validateToken(String token, String username) {
        try {
            return (username.equals(extractUsername(token)) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }


    
//    public boolean validateToken(String token, String username) {
//        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
//    }
    
    public String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }




}