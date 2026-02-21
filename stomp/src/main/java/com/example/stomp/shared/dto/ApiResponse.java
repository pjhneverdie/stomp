package com.example.stomp.shared.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import com.example.stomp.shared.dto.exception.AppException;

public interface ApiResponse<T> {

    T value();

    String message();

    record Success<T>(T value, String message) implements ApiResponse<T> {
        public ResponseEntity<Success<T>> toResponseEntity() {
            return ResponseEntity.ok(this);
        }
    }

    record Failure(Void value, String exceptionName, String message, HttpStatus status) implements ApiResponse<Void> {
        public Failure {
            Assert.isNull(value, "value should be null");
        }

        public ResponseEntity<Failure> toResponseEntity() {
            return ResponseEntity.status(this.status).body(this);
        }
    }

    static <T> Success<Void> createEmptySuccessResponse() {
        return new Success<>(null, "ok");
    }

    static <T> Success<T> createDefaultSuccessResponse(T value) {
        return new Success<>(value, "ok");
    }

    static Failure createDefaultFailureResponse(AppException e) {
        return new Failure(null, e.getClass().getSimpleName(), e.getMessage(), e.getHttpStatus());
    }

}
