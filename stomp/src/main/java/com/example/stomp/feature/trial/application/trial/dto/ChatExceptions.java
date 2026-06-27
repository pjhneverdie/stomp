package com.example.stomp.feature.trial.application.trial.dto;

import org.springframework.http.HttpStatus;

import com.example.stomp.shared.exception.ExceptionInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChatExceptions implements ExceptionInfo {
    ONGOING_CHAT_EXISTS(HttpStatus.BAD_REQUEST, "ongoing trial exists."),
    UNEXISTS_CHAT_ROOM_MEMBER(HttpStatus.BAD_REQUEST, "Unexisting member."),
    UNEXISTS_CHAT(HttpStatus.BAD_REQUEST, "Multiple session is not allowed"),
    MAX_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "You're not the invited"),
    MULTIPLE_WS_CONNECTION_DETECTED(HttpStatus.BAD_REQUEST, "Multiple connection is not allowed");

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