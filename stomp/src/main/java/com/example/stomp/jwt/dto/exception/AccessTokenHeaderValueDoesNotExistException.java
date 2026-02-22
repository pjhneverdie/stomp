package com.example.stomp.jwt.dto.exception;

import org.springframework.http.HttpStatus;

import com.example.stomp.shared.dto.exception.AppException;

public class AccessTokenHeaderValueDoesNotExistException extends AppException {

    public AccessTokenHeaderValueDoesNotExistException() {
        super("");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

}
