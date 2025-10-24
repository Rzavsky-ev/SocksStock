package org.skypro.socksStock.controller;

import org.skypro.socksStock.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений для контроллеров приложения.
 */
@ControllerAdvice
public class SocksStockControllerAdvice {

    /**
     * Обрабатывает исключение EmptyDataException.
     * Возникает при отсутствии необходимых данных в запросе.
     *
     * @param e перехваченное исключение EmptyDataException
     * @return ResponseEntity с сообщением об ошибке и статусом из исключения
     */
    @ExceptionHandler(EmptyDataException.class)
    public ResponseEntity<String> handleEmptyDataException(EmptyDataException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    /**
     * Обрабатывает исключение InvalidQuantityException.
     * Возникает при указании некорректного количества.
     *
     * @param e перехваченное исключение InvalidQuantityException
     * @return ResponseEntity с сообщением об ошибке и статусом из исключения
     */
    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<String> handleInvalidQuantityException(InvalidQuantityException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    /**
     * Обрабатывает исключение SocksNotFoundException.
     * Возникает при попытке доступа к носкам, которые не найдены в системе.
     *
     * @param e перехваченное исключение SocksNotFoundException
     * @return ResponseEntity с сообщением об ошибке и статусом из исключения
     */
    @ExceptionHandler(SocksNotFoundException.class)
    public ResponseEntity<String> handleSocksNotFoundException(SocksNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    /**
     * Обрабатывает исключение InvalidPasswordException.
     * Возникает при указании некорректного пароля (например, несоответствие требованиям безопасности).
     *
     * @param e перехваченное исключение InvalidPasswordException
     * @return ResponseEntity с сообщением об ошибке и статусом из исключения
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    /**
     * Обрабатывает исключение UsernameAlreadyExistsException.
     * Возникает при попытке регистрации пользователя с уже существующим именем.
     *
     * @param e перехваченное исключение UsernameAlreadyExistsException
     * @return ResponseEntity с сообщением об ошибке и статусом из исключения
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    /**
     * Обрабатывает исключение UserNotFoundException.
     * Возникает при попытке доступа к пользователю, который не найден в системе.
     *
     * @param e перехваченное исключение UserNotFoundException
     * @return ResponseEntity с сообщением об ошибке и статусом из исключения
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }
}