package org.skypro.socksStock.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое при попытке доступа к пользователю, который не найден в системе.
 */
@Getter
public class UserNotFoundException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Создает новое исключение с указанным сообщением и HTTP-статусом.
     *
     * @param message детальное сообщение об ошибке, описывающее причину исключения
     * @param status  HTTP-статус, который должен быть возвращен клиенту
     */
    public UserNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}