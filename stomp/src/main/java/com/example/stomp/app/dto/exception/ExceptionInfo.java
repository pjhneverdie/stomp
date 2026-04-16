package com.example.stomp.app.dto.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionInfo {
    HttpStatus getHttpStatus();

    String getMessage();
}
