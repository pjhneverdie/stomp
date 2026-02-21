package com.example.stomp.shared.dto.exception;

import org.springframework.http.HttpStatus;

public abstract class AppException extends RuntimeException {

    public abstract HttpStatus getHttpStatus();

    protected AppException(String message) {
        super(message);
    }

}
