package com.example.stomp.chat.dto.exception;

import org.springframework.http.HttpStatus;

import com.example.stomp.app.dto.exception.ExceptionInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChatExceptions implements ExceptionInfo {
    UNEXISTS_CHAT(HttpStatus.BAD_REQUEST, "Multiple session is not allowed"),
    UNMATCHABLE_MEMBER_CODE(HttpStatus.BAD_REQUEST, "You're not the invited"),
    MULTIPLE_WS_SESSION_DETECTED(HttpStatus.BAD_REQUEST, "Multiple session is not allowed");

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