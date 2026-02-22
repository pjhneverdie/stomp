package com.example.stomp.jwt.dto;

public record RefreshTokenDto(String refreshToken, long maxAgeSec) {

}
