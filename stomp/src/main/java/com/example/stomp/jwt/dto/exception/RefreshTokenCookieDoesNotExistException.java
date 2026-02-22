package com.example.stomp.jwt.dto.exception;

import org.springframework.http.HttpStatus;

import com.example.stomp.shared.dto.exception.AppException;

public class RefreshTokenCookieDoesNotExistException extends AppException {

    public RefreshTokenCookieDoesNotExistException() {
        super("");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

}
