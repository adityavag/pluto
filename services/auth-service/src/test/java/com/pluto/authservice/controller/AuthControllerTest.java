package com.pluto.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pluto.authservice.config.JwtAuthenticationFilter;
import com.pluto.authservice.config.JwtUtil;
import com.pluto.authservice.dto.request.LoginRequest;
import com.pluto.authservice.dto.request.RegisterRequest;
import com.pluto.authservice.dto.response.AuthResponse;
import com.pluto.authservice.dto.response.UserResponse;
import com.pluto.authservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.Import;

@WebMvcTest(AuthController.class)
@Import(JwtAuthenticationFilter.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void testRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        UserResponse response = new UserResponse();
        response.setUserId(1);
        response.setUsername("testuser");
        response.setEmail("test@example.com");
        response.setRole("user");

        Mockito.when(userService.registerUser(Mockito.any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/account/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    public void testLoginUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(1);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setRole("user");

        AuthResponse authResponse = new AuthResponse("mock-jwt-token", userResponse);

        Mockito.when(userService.loginUser(Mockito.any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/account/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.user.userId").value(1));
    }
}
