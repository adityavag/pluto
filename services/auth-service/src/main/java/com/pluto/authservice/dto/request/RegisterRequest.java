package com.pluto.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "username is required.")
    private String username;

    @NotBlank(message = "email is required.")
    @Email(message = "Must be a valid email.")
    private String email;

    @NotBlank(message = "password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password;
}
