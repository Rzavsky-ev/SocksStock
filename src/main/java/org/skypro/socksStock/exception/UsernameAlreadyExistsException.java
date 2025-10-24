package org.skypro.socksStock.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UsernameAlreadyExistsException extends RuntimeException {

    private final HttpStatus status;

    public UsernameAlreadyExistsException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}