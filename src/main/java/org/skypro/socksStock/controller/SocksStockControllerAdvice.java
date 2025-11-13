package org.skypro.socksStock.controller;

import org.skypro.socksStock.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
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

    /**
     * Обрабатывает исключения аутентификации Spring Security.
     * Возникает при неудачной попытке аутентификации пользователя в системе,
     * например, при вводе неверного имени пользователя или пароля.
     *
     * @param e перехваченное исключение AuthenticationException, содержащее информацию об ошибке аутентификации
     * @return ResponseEntity с HTTP-статусом 401 (Unauthorized) и сообщением "Invalid credentials"
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    /**
     * Обрабатывает конфликт имен пользователей при регистрации.
     * Возвращает HTTP-статус 409 (Conflict) с сообщением о занятом имени пользователя.
     *
     * @param e исключение с информацией о занятом имени пользователя
     * @return ResponseEntity со статусом конфликта и сообщением об ошибке
     */
    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<String> handleUsernameAlreadyTakenException(UsernameAlreadyTakenException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }
}