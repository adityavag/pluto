package com.pluto.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @NotBlank(message = "email is required.")
    @Email(message = "Must be a valid email.")
    private String email;
}
