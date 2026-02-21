package com.example.stomp.jwt.dto.exception;

import org.springframework.http.HttpStatus;

import com.example.stomp.shared.dto.exception.AppException;

public class InvalidTokenException extends AppException {

    public InvalidTokenException() {
        super("you are using invalid token");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

}