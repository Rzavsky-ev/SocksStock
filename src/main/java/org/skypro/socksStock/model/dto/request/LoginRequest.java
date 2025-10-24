package org.skypro.socksStock.model.dto.request;


/**
 * DTO для запроса аутентификации пользователя.
 *
 * @param username имя пользователя (логин) для аутентификации
 * @param password пароль пользователя для проверки подлинности
 */
public record LoginRequest(String username, String password) {
}