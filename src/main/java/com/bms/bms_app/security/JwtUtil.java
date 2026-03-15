package com.bms.bms_app.security;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;   
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private Claims extractClaims(String token) {
        log.debug("Attempting to parse JWT token for claims extraction");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("JWT token parsed successfully. Subject: {}, IssuedAt: {}, Expiration: {}", 
                      claims.getSubject(), claims.getIssuedAt(), claims.getExpiration());
            return claims;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired. Subject: {}, IssuedAt: {}, Expiration: {}", 
                     e.getClaims().getSubject(), e.getClaims().getIssuedAt(), e.getClaims().getExpiration());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
            throw e;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("JWT token signature is invalid: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            throw e;
        }
    }

    // Generate JWT token   
    public String generateToken(String email, String role) {
        log.debug("Generating JWT token for email: {}, role: {}", email, role);
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
        log.debug("JWT token generated successfully");
        return token;
    }

    // Extract Email
    public String extractEmail(String token) {
        log.debug("Extracting email from JWT token");
        String email = extractClaims(token).getSubject();
        log.debug("Extracted email: {}", email);
        return email;
    }

    // Validate Token
    public boolean validateToken(String token) {
        log.debug("Starting JWT token validation");
        try {
            extractClaims(token);
            log.debug("JWT token validation successful");
            return true;
        } catch (JwtException e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

}
