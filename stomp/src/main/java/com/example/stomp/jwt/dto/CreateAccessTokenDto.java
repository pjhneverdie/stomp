package com.example.stomp.jwt.dto;

public record CreateAccessTokenDto(
        long memberId,
        String stringAuthorities) {
}
