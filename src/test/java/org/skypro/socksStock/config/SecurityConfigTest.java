package org.skypro.socksStock.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.socksStock.security.JwtAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilterMock;

    @Mock
    private AuthenticationConfiguration authenticationConfigurationMock;

    @DisplayName("Должен создать бин PasswordEncoder")
    @Test
    void createPasswordEncoderBean() {
        SecurityConfig securityConfig = new SecurityConfig(jwtAuthenticationFilterMock);

        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }

    @DisplayName("Должен создать бин AuthenticationManager")
    @Test
    void createAuthenticationManagerBean() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig(jwtAuthenticationFilterMock);
        AuthenticationManager expectedAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfigurationMock.getAuthenticationManager()).thenReturn(expectedAuthManager);

        AuthenticationManager authManager = securityConfig.authenticationManager(authenticationConfigurationMock);

        assertNotNull(authManager);
        assertEquals(expectedAuthManager, authManager);
        verify(authenticationConfigurationMock, times(1)).getAuthenticationManager();
    }
}