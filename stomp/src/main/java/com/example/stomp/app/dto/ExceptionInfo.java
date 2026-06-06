package com.example.stomp.app.dto;

import org.springframework.http.HttpStatus;

public interface ExceptionInfo {

    HttpStatus getHttpStatus();

    String getMessage();

}
