package com.example.stomp.jwt.dto;

public record CreateAccessTokenDto(
        String email,
        String stringAuthorities) {
}
