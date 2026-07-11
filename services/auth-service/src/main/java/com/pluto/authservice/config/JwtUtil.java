package com.pluto.authservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private String getAdminSecret() {
        String secret = System.getProperty("JWT_ADMIN_SECRET");
        if (secret == null || secret.trim().isEmpty()) {
            secret = System.getProperty("JWT_SECRET");
        }
        return secret;
    }

    private String getUserSecret() {
        String secret = System.getProperty("JWT_USER_SECRET");
        if (secret == null || secret.trim().isEmpty()) {
            secret = System.getProperty("JWT_SECRET");
        }
        return secret;
    }

    private Key getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Integer userId, String email, String role) {
        boolean isAdmin = "admin".equalsIgnoreCase(role);
        String secret = isAdmin ? getAdminSecret() : getUserSecret();
        
        String expiresConf;
        if (isAdmin) {
            expiresConf = System.getProperty("JWT_ADMIN_EXPIRES_IN");
            if (expiresConf == null) expiresConf = System.getProperty("JWT_EXPIRES_IN");
        } else {
            expiresConf = System.getProperty("JWT_USER_EXPIRES_IN");
            if (expiresConf == null) expiresConf = System.getProperty("JWT_EXPIRES_IN");
        }
        
        long expiryMs = parseExpiration(expiresConf);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getSigningKey(secret))
                .compact();
    }

    public Claims validateAndExtractClaims(String token) {
        // Try admin secret first, then fallback to user secret
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(getAdminSecret()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException adminEx) {
            try {
                return Jwts.parserBuilder()
                        .setSigningKey(getSigningKey(getUserSecret()))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } catch (JwtException userEx) {
                throw new JwtException("Invalid or expired token.", userEx);
            }
        }
    }

    private long parseExpiration(String exp) {
        if (exp == null || exp.trim().isEmpty()) {
            return 15 * 60 * 1000L; // default 15 minutes
        }
        exp = exp.trim().toLowerCase();
        try {
            if (exp.endsWith("m")) {
                return Long.parseLong(exp.substring(0, exp.length() - 1)) * 60 * 1000L;
            } else if (exp.endsWith("h")) {
                return Long.parseLong(exp.substring(0, exp.length() - 1)) * 60 * 60 * 1000L;
            } else if (exp.endsWith("s")) {
                return Long.parseLong(exp.substring(0, exp.length() - 1)) * 1000L;
            } else if (exp.endsWith("d")) {
                return Long.parseLong(exp.substring(0, exp.length() - 1)) * 24 * 60 * 60 * 1000L;
            } else {
                return Long.parseLong(exp); // Assume milliseconds or seconds
            }
        } catch (NumberFormatException e) {
            return 15 * 60 * 1000L;
        }
    }
}
