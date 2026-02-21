package com.example.stomp.jwt.service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.stomp.jwt.config.JwtContants;
import com.example.stomp.jwt.config.JwtProperties;
import com.example.stomp.jwt.dto.CreateAccessTokenDto;
import com.example.stomp.jwt.dto.CreateRefreshTokenDto;
import com.example.stomp.jwt.dto.CreateRefreshTokenResponse;
import com.example.stomp.jwt.dto.exception.BlacklistedTokenException;
import com.example.stomp.jwt.dto.exception.ExpiredTokenException;
import com.example.stomp.jwt.dto.exception.InvalidTokenException;
import com.example.stomp.jwt.repository.TokenRepository;
import com.example.stomp.jwt.service.provider.JwtProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final TokenRepository tokenRepository;

    // 밖에 제공용
    public Long getMemberId(String token) {
        return Long.parseLong(jwtProvider.parseClaims(token).getSubject());
    }

    // 엑세스 토큰 생성 메서드
    public String createAccessToken(CreateAccessTokenDto cTokenDto) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(JwtContants.ROLES_KEY, cTokenDto.stringAuthorities());
        claims.put(JwtContants.TYPE_DISCRIMINATOR_KEY, JwtContants.TokenType.ACCESS.toString());

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.accessTokenValidity());

        return jwtProvider.createToken(
                cTokenDto.memberId(),
                claims,
                Date.from(now),
                Date.from(expiry));
    }

    // 리프레시 토큰 생성 및 저장 메서드
    public CreateRefreshTokenResponse createAndSaveRefreshToken(CreateRefreshTokenDto cTokenDto) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(JwtContants.TYPE_DISCRIMINATOR_KEY,
                JwtContants.TokenType.REFRESH.toString());

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.refreshTokenValidity());

        String refreshToken = jwtProvider.createToken(cTokenDto.memberId(),
                claims,
                Date.from(now),
                Date.from(expiry));

        tokenRepository.saveRefreshToken(cTokenDto.memberId(),
                refreshToken,
                jwtProperties.refreshTokenValidity());

        return new CreateRefreshTokenResponse(refreshToken, jwtProperties.refreshTokenValidity() / 1000);
    }

    // 엑세스 토큰 블랙리스트 등록 메서드
    public void blackAccessToken(String accessToken, JwtContants.BlackReason reason) {
        long expirationTime = jwtProvider.parseClaims(accessToken).getExpiration().getTime();
        long now = System.currentTimeMillis();

        if (expirationTime > now) {
            tokenRepository.blackAccessToken(accessToken, reason.toString(), expirationTime - now);
        }
    }

    // 리프레시 토큰 삭제 메서드
    public void deleteRefreshToken(String token) {
        tokenRepository.deleteRefreshToken(token);
    }

    // 토큰 검증 메서드
    public boolean validateToken(String token) {
        Claims claims;

        try {
            claims = jwtProvider.parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            throw new InvalidTokenException();
        }

        if (JwtContants.TokenType.ACCESS.toString().equals(claims.get(JwtContants.TYPE_DISCRIMINATOR_KEY))
                && tokenRepository.isBlackedAccessToken(token)) {
            throw new BlacklistedTokenException();
        }

        if (JwtContants.TokenType.REFRESH.toString().equals(claims.get(JwtContants.TYPE_DISCRIMINATOR_KEY))
                && !tokenRepository.doesExistRefreshToken(token)) {
            throw new InvalidTokenException();
        }

        return true;
    }

}
