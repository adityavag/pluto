package com.pluto.authservice.dto.response;

import lombok.Data;
import java.time.Instant;

@Data
public class UserResponse {
    private Integer userId;
    private String username;
    private String email;
    private String role;
    private Instant createdAt;
    private Instant updatedAt;
}
