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

    private String getSecret() {
        String secret = System.getProperty("JWT_SECRET");
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable is not set");
        }
        return secret;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Integer userId, String username, String email, String role) {
        String expiresConf = System.getProperty("JWT_EXPIRES_IN");
        long expiryMs = parseExpiration(expiresConf);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims validateAndExtractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new JwtException("Invalid or expired token.", e);
        }
    }

    /** Convenience extractor for the username claim. */
    public String extractUsername(String token) {
        return validateAndExtractClaims(token).get("username", String.class);
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
                return Long.parseLong(exp);
            }
        } catch (NumberFormatException e) {
            return 15 * 60 * 1000L;
        }
    }
}
