package com.example.stomp.shared.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionInfo {

    HttpStatus getHttpStatus();

    String getMessage();

}
