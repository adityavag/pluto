package com.pluto.authservice.service.impl;

import com.pluto.authservice.config.JwtUtil;
import com.pluto.authservice.dto.request.*;
import com.pluto.authservice.dto.response.AuthResponse;
import com.pluto.authservice.dto.response.UserResponse;
import com.pluto.authservice.entity.User;
import com.pluto.authservice.repository.UserRepository;
import com.pluto.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final int RESET_TOKEN_EXPIRY_MS = 15 * 60 * 1000;

    @Override
    public UserResponse registerUser(RegisterRequest request) {
        String emailClean = request.getEmail().toLowerCase().trim();
        String usernameClean = request.getUsername().trim();

        if (userRepository.findByEmail(emailClean).isPresent() || userRepository.findByUsername(usernameClean).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or email already exists.");
        }

        User user = User.builder()
                .username(usernameClean)
                .email(emailClean)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("user") // Default to regular user
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        String emailClean = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(emailClean)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole());
        return new AuthResponse(token, mapToResponse(user));
    }

    @Override
    public void requestPasswordReset(PasswordResetRequest request) {
        String emailClean = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(emailClean).orElse(null);

        // Standard Node behavior: return 200 with generic message even if email not registered to prevent email enumeration
        if (user == null) {
            return;
        }

        String resetToken = generateResetToken();
        String resetTokenHash = hashResetToken(resetToken);

        user.setResetTokenHash(resetTokenHash);
        user.setResetTokenExpiresAt(Instant.now().plusMillis(RESET_TOKEN_EXPIRY_MS));
        userRepository.save(user);

        if (!"production".equalsIgnoreCase(System.getProperty("NODE_ENV"))) {
            System.out.println("[Password Reset] token: " + resetToken);
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String tokenHash = hashResetToken(request.getToken());
        User user = userRepository.findByResetTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token."));

        if (user.getResetTokenExpiresAt() == null || Instant.now().isAfter(user.getResetTokenExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setResetTokenHash(null);
        user.setResetTokenExpiresAt(null);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Integer userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private String generateResetToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        StringBuilder hexString = new StringBuilder();
        for (byte b : randomBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String hashResetToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
