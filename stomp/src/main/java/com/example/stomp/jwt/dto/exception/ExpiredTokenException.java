package com.example.stomp.jwt.dto.exception;

import org.springframework.http.HttpStatus;

import com.example.stomp.shared.dto.exception.AppException;

public class ExpiredTokenException extends AppException {

    public ExpiredTokenException() {
        super("you are using expired jwt");

    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

}
