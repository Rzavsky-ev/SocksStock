package org.skypro.socksStock.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.socksStock.model.dto.request.LoginRequest;
import org.skypro.socksStock.model.dto.response.AuthResponse;
import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.repository.UserRepository;
import org.skypro.socksStock.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManagerMock;

    @Mock
    private JwtTokenProvider tokenProviderMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @Mock
    private Authentication authenticationMock;

    @InjectMocks
    private AuthService authServiceTest;

    private final String TEST_USERNAME = "testUser";
    private final String TEST_PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "encodedPassword123";
    private final String TEST_JWT_TOKEN = "test.jwt.token";
    private final Role TEST_ROLE = Role.ROLE_USER;

    @Test
    @DisplayName("Аутентификация пользователя с валидными учетными данными должна возвращать токен")
    void authenticateUserWithValidCredentialsReturnToken() {
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(authenticationMock.getName()).thenReturn(TEST_USERNAME);
        when(tokenProviderMock.generateToken(TEST_USERNAME)).thenReturn(ENCODED_PASSWORD);

        AuthResponse response = authServiceTest.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals(ENCODED_PASSWORD, response.token());

        verify(authenticationManagerMock).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProviderMock).generateToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("Аутентификация пользователя должна вызывать AuthenticationManager с корректными учетными данными")
    void authenticateUserCallAuthenticationManagerWithCorrectCredentials() {
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(authenticationMock.getName()).thenReturn(TEST_USERNAME);
        when(tokenProviderMock.generateToken(TEST_USERNAME)).thenReturn("token");

        authServiceTest.authenticateUser(loginRequest);

        verify(authenticationManagerMock).authenticate(
                argThat((UsernamePasswordAuthenticationToken token) ->
                        TEST_USERNAME.equals(token.getPrincipal()) &&
                                TEST_PASSWORD.equals(token.getCredentials()))
        );
    }

    @Test
    @DisplayName("Аутентификация пользователя должна генерировать токен с корректным именем пользователя")
    void authenticateUserGenerateTokenWithCorrectUsername() {
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(authenticationMock.getName()).thenReturn(TEST_USERNAME);
        when(tokenProviderMock.generateToken(TEST_USERNAME)).thenReturn("token");

        authServiceTest.authenticateUser(loginRequest);

        verify(tokenProviderMock).generateToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("Аутентификация пользователя с невалидными учетными данными должна выбрасывать исключение")
    void authenticateUserWithInvalidCredentialsThrowException() {
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authServiceTest.authenticateUser(loginRequest));

        verify(tokenProviderMock, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Аутентификация пользователя с пустыми учетными данными должна выбрасывать исключение")
    void authenticateUserWithEmptyCredentialsThrowException() {
        LoginRequest loginRequest = new LoginRequest("", "");

        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Empty credentials"));

        assertThrows(BadCredentialsException.class, () -> authServiceTest.authenticateUser(loginRequest));
    }

    @Test
    @DisplayName("Аутентификация пользователя должна возвращать корректную структуру AuthResponse")
    void authenticateUserReturnCorrectAuthResponseStructure() {
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(authenticationMock.getName()).thenReturn(TEST_USERNAME);
        when(tokenProviderMock.generateToken(TEST_USERNAME)).thenReturn(TEST_JWT_TOKEN);

        AuthResponse response = authServiceTest.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals(TEST_JWT_TOKEN, response.token());
        assertInstanceOf(AuthResponse.class, response);
    }

    @Test
    @DisplayName("Регистрация пользователя с новым именем должна успешно завершиться")
    void registerUserWithNewUsernameSuccessfullyRegisterUser() {
        when(userRepositoryMock.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        when(passwordEncoderMock.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn(TEST_USERNAME);
        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(tokenProviderMock.generateToken(TEST_USERNAME)).thenReturn(TEST_JWT_TOKEN);

        AuthResponse result = authServiceTest.registerUser(TEST_USERNAME, TEST_PASSWORD);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(TEST_JWT_TOKEN, result.token());

        verify(userRepositoryMock).findByUsername(TEST_USERNAME);
        verify(passwordEncoderMock).encode(TEST_PASSWORD);
        verify(userRepositoryMock).save(any(AppUser.class));

        verify(userRepositoryMock).save(argThat(user ->
                user.getUsername().equals(TEST_USERNAME) &&
                        user.getPassword().equals(ENCODED_PASSWORD) &&
                        user.getRole() == TEST_ROLE
        ));

        verify(authenticationManagerMock).authenticate(argThat(authToken ->
                authToken.getPrincipal().equals(TEST_USERNAME) &&
                        authToken.getCredentials().equals(TEST_PASSWORD)
        ));

        verify(tokenProviderMock).generateToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("Регистрация пользователя с существующим именем должна выбрасывать исключение")
    void registerUserWithExistingUsernameThrowException() {
        AppUser existingUser = new AppUser();
        existingUser.setUsername(TEST_USERNAME);
        existingUser.setPassword(ENCODED_PASSWORD);

        when(userRepositoryMock.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authServiceTest.registerUser(TEST_USERNAME, TEST_PASSWORD));

        assertEquals("Username is already taken", exception.getMessage());

        verify(userRepositoryMock, never()).save(any(AppUser.class));
        verify(passwordEncoderMock, never()).encode(anyString());
        verify(authenticationManagerMock, never()).authenticate(any());
        verify(tokenProviderMock, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Регистрация пользователя должна устанавливать корректную роль пользователя")
    void registerUserSetCorrectUserRole() {
        when(userRepositoryMock.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn(TEST_USERNAME);
        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(tokenProviderMock.generateToken(TEST_USERNAME)).thenReturn("token");

        authServiceTest.registerUser(TEST_USERNAME, TEST_PASSWORD);

        verify(userRepositoryMock).save(argThat(user ->
                user.getRole() == TEST_ROLE));
    }

    @Test
    @DisplayName("Регистрация пользователя должна кодировать пароль")
    void registerUserEncodePassword() {
        when(userRepositoryMock.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        when(authenticationMock.getName()).thenReturn(TEST_USERNAME);
        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(tokenProviderMock.generateToken(TEST_USERNAME)).thenReturn("token");

        authServiceTest.registerUser(TEST_USERNAME, TEST_PASSWORD);

        verify(passwordEncoderMock).encode(TEST_PASSWORD);
        verify(userRepositoryMock).save(argThat(user ->
                user.getPassword().equals(ENCODED_PASSWORD)));
    }

    @Test
    @DisplayName("Регистрация пользователя с null именем должна выбрасывать исключение")
    void registerUserWithNullUsernameThrowException() {
        assertThrows(Exception.class,
                () -> authServiceTest.registerUser(null, "password"));
    }

    @Test
    @DisplayName("Регистрация пользователя с пустым именем должна выбрасывать исключение")
    void registerUserWithEmptyUsernameThrowException() {
        assertThrows(Exception.class,
                () -> authServiceTest.registerUser("", "password"));
    }

    @Test
    @DisplayName("Регистрация пользователя с null паролем должна выбрасывать исключение")
    void registerUserWithNullPasswordThrowException() {
        assertThrows(Exception.class,
                () -> authServiceTest.registerUser("username", null));
    }

    @Test
    @DisplayName("Регистрация пользователя с валидными данными должна возвращать AuthResponse")
    void registerUserWhenValidDataReturnAuthResponse() {
        when(userRepositoryMock.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        when(authenticationMock.getName()).thenReturn(TEST_USERNAME);
        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(tokenProviderMock.generateToken(TEST_USERNAME)).thenReturn(TEST_JWT_TOKEN);

        AuthResponse result = authServiceTest.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_ROLE);

        assertNotNull(result);
        assertEquals(TEST_JWT_TOKEN, result.token());

        verify(userRepositoryMock).findByUsername(TEST_USERNAME);
        verify(passwordEncoderMock).encode(TEST_PASSWORD);
        verify(userRepositoryMock).save(any(AppUser.class));
        verify(authenticationManagerMock).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProviderMock).generateToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("Регистрация пользователя с существующим именем должна выбрасывать исключение (перегруженный метод)")
    void registerUserWhenUsernameAlreadyExistsThrowException() {
        AppUser existingUser = new AppUser();
        existingUser.setUsername(TEST_USERNAME);
        when(userRepositoryMock.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authServiceTest.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_ROLE));

        assertEquals("Username is already taken", exception.getMessage());

        verify(userRepositoryMock, never()).save(any(AppUser.class));
        verify(passwordEncoderMock, never()).encode(anyString());
    }

    @Test
    @DisplayName("После успешной регистрации пользователь должен аутентифицироваться")
    void registerUserAfterSuccessfulRegistrationAuthenticateUser() {
        when(userRepositoryMock.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepositoryMock.save(any(AppUser.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        when(authenticationMock.getName()).thenReturn(TEST_USERNAME);
        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);
        when(tokenProviderMock.generateToken(TEST_USERNAME)).thenReturn(TEST_JWT_TOKEN);

        authServiceTest.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_ROLE);

        verify(authenticationManagerMock).authenticate(argThat(authToken ->
                authToken instanceof UsernamePasswordAuthenticationToken &&
                        TEST_USERNAME.equals((authToken).getPrincipal()) &&
                        TEST_PASSWORD.equals((authToken).getCredentials())
        ));
    }

    @Test
    @DisplayName("Регистрация пользователя с null именем должна выбрасывать исключение (перегруженный метод)")
    void registerUserWhenUsernameIsNullThrowException() {
        assertThrows(Exception.class,
                () -> authServiceTest.registerUser(null, TEST_PASSWORD, TEST_ROLE));
    }

    @Test
    @DisplayName("Регистрация пользователя с null паролем должна выбрасывать исключение (перегруженный метод)")
    void registerUserWhenPasswordIsNullThrowException() {
        assertThrows(Exception.class,
                () -> authServiceTest.registerUser(TEST_USERNAME, null, TEST_ROLE));
    }

    @Test
    @DisplayName("Регистрация пользователя с null ролью должна выбрасывать исключение")
    void registerUserWhenRoleIsNullThrowException() {
        assertThrows(Exception.class,
                () -> authServiceTest.registerUser(TEST_USERNAME, TEST_PASSWORD, null));
    }
}

