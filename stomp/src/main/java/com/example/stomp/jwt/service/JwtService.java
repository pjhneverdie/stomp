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
import com.example.stomp.jwt.repository.RedisTokenRepository;
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
    private final RedisTokenRepository tokenRepository;

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

    public void blackAccessToken(String accessToken, JwtContants.BlackReason reason) {
        tokenRepository.blackAccessToken(accessToken, reason.toString(), jwtProperties.accessTokenValidity());
    }

    public void deleteRefreshToken(String token) {
        tokenRepository.deleteRefreshToken(token);
    }

    public Claims validateToken(String token) {
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

        return claims;
    }

}
