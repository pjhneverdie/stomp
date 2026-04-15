package com.example.stomp.chat.dto.exception;

import org.springframework.http.HttpStatus;

import com.example.stomp.app.dto.exception.ExceptionSchema;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChatExceptionSchema implements ExceptionSchema {

    UNMATCHABLE_MEMBER_CODE(HttpStatus.BAD_REQUEST, "초대받지 않은 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

}