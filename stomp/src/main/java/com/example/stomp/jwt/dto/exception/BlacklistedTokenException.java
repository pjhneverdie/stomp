package com.example.stomp.jwt.dto.exception;

import org.springframework.http.HttpStatus;

import com.example.stomp.shared.dto.exception.AppException;

public class BlacklistedTokenException extends AppException {

    public BlacklistedTokenException() {
        super("you are using blacklisted jwt");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

}
