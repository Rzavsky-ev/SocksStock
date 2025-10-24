package org.skypro.socksStock.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое при попытке использования некорректного пароля.
 */
@Getter
public class InvalidPasswordException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Создает новое исключение с указанным сообщением и HTTP-статусом.
     *
     * @param message детальное сообщение об ошибке, описывающее причину исключения
     * @param status  HTTP-статус, который должен быть возвращен клиенту
     */
    public InvalidPasswordException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}