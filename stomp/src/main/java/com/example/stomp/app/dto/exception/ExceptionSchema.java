package com.example.stomp.app.dto.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionSchema {
    HttpStatus getHttpStatus();

    String getMessage();
}
