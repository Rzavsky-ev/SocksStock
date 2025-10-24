package org.skypro.socksStock.controller;

import lombok.RequiredArgsConstructor;
import org.skypro.socksStock.model.dto.request.LoginRequest;
import org.skypro.socksStock.model.dto.response.AuthResponse;
import org.skypro.socksStock.model.entity.Role;
import org.skypro.socksStock.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для обработки запросов аутентификации и регистрации пользователей.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Аутентифицирует пользователя в системе.
     * Проверяет учетные данные и возвращает JWT-токен при успешной аутентификации.
     *
     * @param loginRequest объект запроса, содержащий имя пользователя и пароль
     * @return ResponseEntity с объектом AuthResponse, содержащим JWT-токен
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Регистрирует нового пользователя с ролью ROLE_USER.
     * Создает учетную запись с указанными именем пользователя и паролем.
     *
     * @param username имя пользователя для регистрации
     * @param password пароль пользователя
     * @return ResponseEntity с объектом AuthResponse, содержащим JWT-токен
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(
            @RequestParam String username,
            @RequestParam String password) {
        AuthResponse response = authService.registerUser(username, password);
        return ResponseEntity.ok(response);
    }

    /**
     * Регистрирует нового пользователя с ролью ROLE_ADMIN.
     * Создает административную учетную запись с указанными именем пользователя и паролем.
     *
     * @param username имя пользователя для регистрации
     * @param password пароль пользователя
     * @return ResponseEntity с объектом AuthResponse, содержащим JWT-токен
     */
    @PostMapping("/register-admin")
    public ResponseEntity<AuthResponse> registerAdmin(
            @RequestParam String username,
            @RequestParam String password) {
        AuthResponse response = authService.registerUser(username, password, Role.ROLE_ADMIN);
        return ResponseEntity.ok(response);
    }
}
