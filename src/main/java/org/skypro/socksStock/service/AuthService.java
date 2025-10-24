package org.skypro.socksStock.service;

import lombok.RequiredArgsConstructor;
import org.skypro.socksStock.model.dto.request.LoginRequest;
import org.skypro.socksStock.model.dto.response.AuthResponse;
import org.skypro.socksStock.model.entity.AppUser;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.repository.UserRepository;
import org.skypro.socksStock.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для обработки операций аутентификации и регистрации пользователей.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Аутентифицирует пользователя в системе.
     *
     * @param loginRequest объект с учетными данными пользователя (имя и пароль)
     * @return AuthResponse с JWT-токеном для доступа к защищенным ресурсам
     * @throws org.springframework.security.core.AuthenticationException если аутентификация не удалась
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication.getName());
        return new AuthResponse(jwt);
    }

    /**
     * Регистрирует нового пользователя с ролью ROLE_USER.
     *
     * @param username имя пользователя для регистрации
     * @param password пароль пользователя
     * @return AuthResponse с JWT-токеном для нового пользователя
     * @throws RuntimeException если пользователь с таким именем уже существует
     */
    public AuthResponse registerUser(String username, String password) {
        return registerUser(username, password, Role.ROLE_USER);
    }

    /**
     * Регистрирует нового пользователя с указанной ролью.
     *
     * @param username имя пользователя для регистрации
     * @param password пароль пользователя
     * @param role     роль пользователя в системе
     * @return AuthResponse с JWT-токеном для нового пользователя
     * @throws RuntimeException если пользователь с таким именем уже существует
     */
    public AuthResponse registerUser(String username, String password, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username is already taken");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setRole(role);

        userRepository.save(appUser);
        return authenticateUser(new LoginRequest(username, password));
    }
}