package com.example.stomp.member.dto;

import org.springframework.http.HttpStatus;

import com.example.stomp.application.dto.ExceptionInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberExceptions implements ExceptionInfo {

    UNEXISTS_MEMBER(HttpStatus.BAD_REQUEST, "Multiple session is not allowed");

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
