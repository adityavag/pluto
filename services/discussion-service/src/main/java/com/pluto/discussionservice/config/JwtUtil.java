package com.pluto.discussionservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * Lightweight JWT parser for the discussion service.
 * Only validates and extracts claims — token generation happens in auth-service.
 * Uses the same single JWT_SECRET as auth-service.
 */
@Component
public class JwtUtil {

    private Key getSigningKey() {
        String secret = System.getProperty("JWT_SECRET");
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable is not set");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims validateAndExtractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new JwtException("Invalid or expired token.");
        }
    }

    /** Extracts the username claim embedded by auth-service at login time. */
    public String extractUsername(String token) {
        return validateAndExtractClaims(token).get("username", String.class);
    }
}
