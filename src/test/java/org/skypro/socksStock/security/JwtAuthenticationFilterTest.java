package org.skypro.socksStock.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProviderMock;

    @Mock
    private CustomUserDetailsService userDetailsServiceMock;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    @Mock
    private FilterChain filterChainMock;

    @Mock
    private UserDetails userDetailsMock;

    @Mock
    private SecurityContext securityContextMock;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilterTest;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContextMock);
    }

    @DisplayName("Должен устанавливать аутентификацию в контекст безопасности при валидном токене")
    @Test
    void shouldSetAuthenticationInSecurityContextForValidToken() throws ServletException, IOException {
        String validToken = "valid.jwt.token";
        String username = "testUser";

        when(requestMock.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(tokenProviderMock.validateToken(validToken)).thenReturn(true);
        when(tokenProviderMock.getUsernameFromToken(validToken)).thenReturn(username);
        when(userDetailsServiceMock.loadUserByUsername(username)).thenReturn(userDetailsMock);
        when(userDetailsMock.getAuthorities()).thenReturn(anyCollection());

        jwtAuthenticationFilterTest.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(securityContextMock).setAuthentication(any(Authentication.class));
        verify(filterChainMock).doFilter(requestMock, responseMock);
    }

    @DisplayName("Должен обрабатывать запросы без заголовка Authorization")
    @Test
    void shouldSkipRequestsWithoutAuthorizationHeader() throws ServletException, IOException {
        when(requestMock.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilterTest.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(tokenProviderMock, never()).validateToken(anyString());
        verify(securityContextMock, never()).setAuthentication(any(Authentication.class));
        verify(filterChainMock).doFilter(requestMock, responseMock);
    }

    @DisplayName("Должен обрабатывать невалидный токен без установки аутентификации")
    @Test
    void shouldSkipRequestsWithInvalidJwtToken() throws ServletException, IOException {
        String invalidToken = "invalid.jwt.token";
        when(requestMock.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(tokenProviderMock.validateToken(invalidToken)).thenReturn(false);

        jwtAuthenticationFilterTest.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(tokenProviderMock).validateToken(invalidToken);
        verify(tokenProviderMock, never()).getUsernameFromToken(anyString());
        verify(userDetailsServiceMock, never()).loadUserByUsername(anyString());
        verify(securityContextMock, never()).setAuthentication(any(Authentication.class));
        verify(filterChainMock).doFilter(requestMock, responseMock);
    }

    @DisplayName("Должен обрабатывать запросы когда заголовок содержит только 'Bearer' без токена")
    @Test
    void shouldSkipRequestsWhenHeaderContainsOnlyBearerWithoutToken() throws ServletException, IOException {
        when(requestMock.getHeader("Authorization")).thenReturn("Bearer");

        jwtAuthenticationFilterTest.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(tokenProviderMock, never()).validateToken(anyString());
        verify(securityContextMock, never()).setAuthentication(any(Authentication.class));
        verify(filterChainMock).doFilter(requestMock, responseMock);
    }

    @Test
    @DisplayName("Должен пропускать запросы когда заголовок содержит 'Bearer' с пробелами но без токена")
    void shouldSkipRequestsWhenHeaderContainsBearerWithSpacesButNoToken() throws ServletException, IOException {
        when(requestMock.getHeader("Authorization")).thenReturn("Bearer   ");

        jwtAuthenticationFilterTest.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(tokenProviderMock, never()).validateToken(anyString());
        verify(securityContextMock, never()).setAuthentication(any(Authentication.class));
        verify(filterChainMock).doFilter(requestMock, responseMock);
    }

    @DisplayName("Должен обрабатывать запросы с различными форматами валидных токенов")
    @Test
    void shouldProcessRequestsWithVariousValidTokenFormats() throws ServletException, IOException {
        String[] validTokens = {"standard.token", "token-with-dashes", "token_with_underscores", "token123"};
        String username = "testUser";

        for (String token : validTokens) {
            reset(tokenProviderMock, userDetailsServiceMock, securityContextMock, requestMock);
            SecurityContextHolder.setContext(securityContextMock);

            when(requestMock.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(tokenProviderMock.validateToken(token)).thenReturn(true);
            when(tokenProviderMock.getUsernameFromToken(token)).thenReturn(username);
            when(userDetailsServiceMock.loadUserByUsername(username)).thenReturn(userDetailsMock);
            when(userDetailsMock.getAuthorities()).thenReturn(List.of());

            jwtAuthenticationFilterTest.doFilterInternal(requestMock, responseMock, filterChainMock);

            verify(tokenProviderMock).validateToken(token);
            verify(tokenProviderMock).getUsernameFromToken(token);
            verify(securityContextMock).setAuthentication(any(Authentication.class));
        }
        verify(filterChainMock, times(validTokens.length)).doFilter(requestMock, responseMock);
    }

    @DisplayName("Должен устанавливать детали аутентификации из HTTP запроса")
    @Test
    void shouldSetAuthenticationDetailsFromHttpRequest() throws ServletException, IOException {
        String validToken = "valid.jwt.token";
        String username = "testUser";

        when(requestMock.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(tokenProviderMock.validateToken(validToken)).thenReturn(true);
        when(tokenProviderMock.getUsernameFromToken(validToken)).thenReturn(username);
        when(userDetailsServiceMock.loadUserByUsername(username)).thenReturn(userDetailsMock);
        when(userDetailsMock.getAuthorities()).thenReturn(List.of());

        jwtAuthenticationFilterTest.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(securityContextMock).setAuthentication(argThat(authentication ->
                authentication != null && authentication.getDetails() != null
        ));
    }

    @DisplayName("Должен обрабатывать случай когда UserDetailsService не находит пользователя")
    @Test
    void shouldHandleCaseWhenUserDetailsServiceDoesNotFindUser() throws ServletException, IOException {
        String validToken = "valid.jwt.token";
        String username = "nonExistentUser";

        when(requestMock.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(tokenProviderMock.validateToken(validToken)).thenReturn(true);
        when(tokenProviderMock.getUsernameFromToken(validToken)).thenReturn(username);
        when(userDetailsServiceMock.loadUserByUsername(username)).thenThrow(new RuntimeException("Пользователь не найден"));

        jwtAuthenticationFilterTest.doFilterInternal(requestMock, responseMock, filterChainMock);

        verify(filterChainMock).doFilter(requestMock, responseMock);
    }
}