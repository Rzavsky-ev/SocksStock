package org.skypro.socksStock.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое при отсутствии необходимых данных в запросе или операции.
 */
@Getter
public class EmptyDataException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Создает новое исключение с указанным сообщением и HTTP-статусом.
     *
     * @param message детальное сообщение об ошибке, описывающее причину исключения
     * @param status  HTTP-статус, который должен быть возвращен клиенту
     */
    public EmptyDataException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
