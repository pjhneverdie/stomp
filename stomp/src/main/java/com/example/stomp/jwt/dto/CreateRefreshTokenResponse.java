package com.example.stomp.jwt.dto;

public record CreateRefreshTokenResponse(String refreshToken, long maxAgeSeconds) {

}
