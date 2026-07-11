package com.pluto.authservice.service;

import com.pluto.authservice.dto.request.*;
import com.pluto.authservice.dto.response.AuthResponse;
import com.pluto.authservice.dto.response.UserResponse;

public interface UserService {
    UserResponse registerUser(RegisterRequest request);
    AuthResponse loginUser(LoginRequest request);
    void requestPasswordReset(PasswordResetRequest request);
    void resetPassword(ResetPasswordRequest request);
    void changePassword(Integer userId, ChangePasswordRequest request);
}
