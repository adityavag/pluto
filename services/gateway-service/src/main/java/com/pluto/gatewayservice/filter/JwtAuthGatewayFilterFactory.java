package com.pluto.gatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {

    public JwtAuthGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Check if Authorization header is present
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            // 2. Validate and extract claims
            try {
                Claims claims = validateAndExtractClaims(token);
                
                Object userIdObj = claims.get("userId");
                if (userIdObj == null) {
                    return onError(exchange, "Invalid token claims: userId is missing", HttpStatus.UNAUTHORIZED);
                }
                String userId = userIdObj.toString();

                // 3. Propagate X-User-Id downstream
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (JwtException e) {
                System.err.println("JWT Verification failed: " + e.getMessage());
                e.printStackTrace();
                return onError(exchange, "Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Claims validateAndExtractClaims(String token) {
        String adminSecret = System.getProperty("JWT_ADMIN_SECRET");
        if (adminSecret == null || adminSecret.trim().isEmpty()) {
            adminSecret = System.getProperty("JWT_SECRET");
        }
        String userSecret = System.getProperty("JWT_USER_SECRET");
        if (userSecret == null || userSecret.trim().isEmpty()) {
            userSecret = System.getProperty("JWT_SECRET");
        }

        if (adminSecret == null || userSecret == null) {
            throw new JwtException("JWT signature keys are not configured in system properties");
        }

        // Try admin secret first, fallback to user secret
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(adminSecret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException adminEx) {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(userSecret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
    }

    private Key getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class Config {
        // config settings if needed
    }
}
