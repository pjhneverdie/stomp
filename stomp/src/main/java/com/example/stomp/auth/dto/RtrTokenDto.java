package com.example.stomp.auth.dto;

import com.example.stomp.jwt.dto.RefreshTokenDto;

public record RtrTokenDto(String accessToken, RefreshTokenDto refreshTokenDto) {
}