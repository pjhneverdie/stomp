package com.example.stomp.application.dto;

import org.springframework.http.HttpStatus;

public interface ExceptionInfo {

    HttpStatus getHttpStatus();

    String getMessage();

}
