package org.skypro.socksStock.model.dto.response;

/**
 * DTO для ответа аутентификации.
 *
 * @param token JWT-токен для аутентификации
 * @param type  тип токена (по умолчанию "Bearer")
 */
public record AuthResponse(String token, String type) {
    public AuthResponse(String token) {
        this(token, "Bearer");
    }
}