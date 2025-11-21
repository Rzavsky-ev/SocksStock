package org.skypro.socksStock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skypro.socksStock.model.dto.request.LoginRequest;
import org.skypro.socksStock.model.dto.response.AuthResponse;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.security.CustomUserDetailsService;
import org.skypro.socksStock.security.JwtTokenProvider;
import org.skypro.socksStock.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.ArgumentMatchers.eq;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authServiceMock;

    @MockBean
    private JwtTokenProvider jwtTokenProviderMock;

    @MockBean
    private CustomUserDetailsService customUserDetailsServiceMock;

    private final String TEST_USERNAME = "testUser";
    private final String TEST_PASSWORD = "testPass";
    private final String TEST_TOKEN = "test.jwt.token";

    @DisplayName("Успешная аутентификация пользователя - должен вернуть JWT токен")
    @Test
    void authenticateUserWhenValidCredentialsReturnJwtToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);
        AuthResponse authResponse = new AuthResponse(TEST_TOKEN);

        given(authServiceMock.authenticateUser(any(LoginRequest.class)))
                .willReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TEST_TOKEN));

        then(authServiceMock).should().authenticateUser(any(LoginRequest.class));
    }

    @DisplayName("Успешная регистрация пользователя - должен вернуть JWT токен")
    @Test
    void registerUserWhenValidDataReturnJwtToken() throws Exception {
        AuthResponse authResponse = new AuthResponse(TEST_TOKEN);

        given(authServiceMock.registerUser(eq(TEST_USERNAME), eq(TEST_PASSWORD)))
                .willReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .param("username", TEST_USERNAME)
                        .param("password", TEST_PASSWORD)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TEST_TOKEN));

        then(authServiceMock).should().registerUser(TEST_USERNAME, TEST_PASSWORD);
    }

    @DisplayName("Успешная регистрация администратора - должен вернуть JWT токен")
    @Test
    void registerAdminWhenValidDataReturnJwtToken() throws Exception {
        AuthResponse authResponse = new AuthResponse(TEST_TOKEN);

        given(authServiceMock.registerUser(eq(TEST_USERNAME), eq(TEST_PASSWORD), eq(Role.ROLE_ADMIN)))
                .willReturn(authResponse);

        mockMvc.perform(post("/api/auth/register-admin")
                        .with(csrf())
                        .param("username", TEST_USERNAME)
                        .param("password", TEST_PASSWORD)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TEST_TOKEN));

        then(authServiceMock).should().registerUser(TEST_USERNAME, TEST_PASSWORD, Role.ROLE_ADMIN);
    }
}

