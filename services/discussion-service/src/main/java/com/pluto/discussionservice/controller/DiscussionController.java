package com.pluto.discussionservice.controller;

import com.pluto.discussionservice.config.JwtUtil;
import com.pluto.discussionservice.dto.request.CreateDiscussionRequest;
import com.pluto.discussionservice.dto.response.DiscussionResponse;
import com.pluto.discussionservice.dto.response.PaginatedResponse;
import com.pluto.discussionservice.service.DiscussionService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/discuss")
@RequiredArgsConstructor
public class DiscussionController {

    private final DiscussionService discussionService;
    private final JwtUtil jwtUtil;

    // ── GET /discuss/ ─────────────────────────────────────────────────────────
    // Returns all general discussions — public, no auth required
    @GetMapping("/")
    public ResponseEntity<PaginatedResponse<DiscussionResponse>> getAllDiscussions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(discussionService.getAllDiscussions(page, size));
    }

    // ── POST /discuss/ ────────────────────────────────────────────────────────
    // Creates a new general discussion — username comes from the JWT
    @PostMapping("/")
    public ResponseEntity<DiscussionResponse> createDiscussion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Valid @RequestBody CreateDiscussionRequest request) {
        String username = extractUsername(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(discussionService.createDiscussion(username, request));
    }

    // ── GET /discuss/post/{postId} ────────────────────────────────────────────
    // Returns all discussions for a specific post — public, no auth required
    @GetMapping("/post/{postId}")
    public ResponseEntity<PaginatedResponse<DiscussionResponse>> getDiscussionsByPost(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(discussionService.getDiscussionsByPostId(postId, page, size));
    }

    // ── POST /discuss/post/{postId} ───────────────────────────────────────────
    // Creates a discussion for a specific post — username comes from the JWT
    @PostMapping("/post/{postId}")
    public ResponseEntity<DiscussionResponse> createDiscussionForPost(
            @PathVariable UUID postId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Valid @RequestBody CreateDiscussionRequest request) {
        String username = extractUsername(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(discussionService.createDiscussionForPost(postId, username, request));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Strips "Bearer " prefix, validates the token, and returns the username claim.
     * Throws 401 if the token is missing, malformed, or invalid.
     */
    private String extractUsername(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            if (username == null || username.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token does not contain a valid username");
            }
            return username;
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }
}
