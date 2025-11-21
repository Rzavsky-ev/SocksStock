package org.skypro.socksStock.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.socksStock.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SocksStockControllerAdviceTest {

    @InjectMocks
    private SocksStockControllerAdvice controllerAdvice;

    @DisplayName("Должен обработать EmptyDataException и вернуть корректный ответ")
    @Test
    void handleEmptyDataExceptionReturnCorrectResponse() {
        String errorMessage = "Пустые данные";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        EmptyDataException exception = new EmptyDataException(errorMessage, status);

        ResponseEntity<String> response = controllerAdvice.handleEmptyDataException(exception);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(errorMessage));
    }

    @DisplayName("Должен обработать InvalidQuantityException и вернуть корректный ответ")
    @Test
    void handleInvalidQuantityExceptionReturnCorrectResponse() {
        String errorMessage = "Количество не может быть отрицательным";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        InvalidQuantityException exception = new InvalidQuantityException(errorMessage, status);

        ResponseEntity<String> response = controllerAdvice.handleInvalidQuantityException(exception);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @DisplayName("Должен обработать SocksNotFoundException и вернуть корректный ответ")
    @Test
    void handleSocksNotFoundExceptionReturnCorrectResponse() {
        String errorMessage = "Носки не найдены";
        HttpStatus status = HttpStatus.NOT_FOUND;
        SocksNotFoundException exception = new SocksNotFoundException(errorMessage, status);

        ResponseEntity<String> response = controllerAdvice.handleSocksNotFoundException(exception);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @DisplayName("Должен обработать InvalidPasswordException и вернуть корректный ответ")
    @Test
    void handleInvalidPasswordExceptionReturnCorrectResponse() {
        String errorMessage = "Пароль не соответствует требованиям безопасности";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        InvalidPasswordException exception = new InvalidPasswordException(errorMessage, status);

        ResponseEntity<String> response = controllerAdvice.handleInvalidPasswordException(exception);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @DisplayName("Должен обработать UsernameAlreadyExistsException и вернуть корректный ответ")
    @Test
    void handleUsernameAlreadyExistsExceptionReturnCorrectResponse() {
        String errorMessage = "Пользователь с таким именем уже существует";
        HttpStatus status = HttpStatus.CONFLICT;
        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException(errorMessage, status);

        ResponseEntity<String> response = controllerAdvice.handleUsernameAlreadyExistsException(exception);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @DisplayName("Должен обработать UserNotFoundException и вернуть корректный ответ")
    @Test
    void handleUserNotFoundExceptionReturnCorrectResponse() {
        String errorMessage = "Пользователь не найден";
        HttpStatus status = HttpStatus.NOT_FOUND;
        UserNotFoundException exception = new UserNotFoundException(errorMessage, status);

        ResponseEntity<String> response = controllerAdvice.handleUserNotFoundException(exception);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @DisplayName("Должен вернуть ResponseEntity с правильным типом и содержимым для EmptyDataException")
    @Test
    void handleEmptyDataExceptionReturnProperResponseEntity() {
        String expectedBody = "Пустые данные";
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        EmptyDataException exception = new EmptyDataException(expectedBody, expectedStatus);

        ResponseEntity<String> response = controllerAdvice.handleEmptyDataException(exception);

        assertAll(
                () -> assertEquals(expectedStatus, response.getStatusCode()),
                () -> assertEquals(expectedBody, response.getBody()),
                () -> assertTrue(response.hasBody()),
                () -> assertNotNull(response.getHeaders())
        );
    }

    @DisplayName("Должен корректно обрабатывать разные HTTP статусы для различных исключений")
    @Test
    void handleDifferentExceptionsReturnAppropriateStatusCodes() {
        HttpStatus[] expectedStatuses = {
                HttpStatus.BAD_REQUEST,
                HttpStatus.NOT_FOUND,
                HttpStatus.CONFLICT
        };

        EmptyDataException emptyDataException = new EmptyDataException("Ошибка данных", expectedStatuses[0]);
        SocksNotFoundException socksNotFoundException = new SocksNotFoundException("Не найдено", expectedStatuses[1]);
        UsernameAlreadyExistsException usernameException = new UsernameAlreadyExistsException("Конфликт", expectedStatuses[2]);

        ResponseEntity<String> response1 = controllerAdvice.handleEmptyDataException(emptyDataException);
        ResponseEntity<String> response2 = controllerAdvice.handleSocksNotFoundException(socksNotFoundException);
        ResponseEntity<String> response3 = controllerAdvice.handleUsernameAlreadyExistsException(usernameException);

        assertEquals(expectedStatuses[0], response1.getStatusCode());
        assertEquals(expectedStatuses[1], response2.getStatusCode());
        assertEquals(expectedStatuses[2], response3.getStatusCode());
    }

    @DisplayName("Должен сохранять оригинальное сообщение исключения в ответе")
    @Test
    void handleExceptionPreserveOriginalExceptionMessage() {
        String detailedMessage = "Поле 'color' не может быть пустым";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        EmptyDataException exception = new EmptyDataException(detailedMessage, status);

        ResponseEntity<String> response = controllerAdvice.handleEmptyDataException(exception);

        assertEquals(detailedMessage, response.getBody());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("'color'"));
    }
}