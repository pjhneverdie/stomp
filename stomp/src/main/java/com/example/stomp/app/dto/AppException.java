package com.example.stomp.app.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AppException extends RuntimeException {

    private final ExceptionInfo exceptionInfo;

}
