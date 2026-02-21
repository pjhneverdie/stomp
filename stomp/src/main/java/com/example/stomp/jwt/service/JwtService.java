package com.example.stomp.jwt.service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.stomp.jwt.config.JwtContants;
import com.example.stomp.jwt.config.JwtProperties;
import com.example.stomp.jwt.dto.CreateAccessTokenDto;
import com.example.stomp.jwt.dto.CreateRefreshTokenDto;
import com.example.stomp.jwt.dto.CreateRefreshTokenResponse;
import com.example.stomp.jwt.dto.exception.ExpiredTokenException;
import com.example.stomp.jwt.dto.exception.InvalidTokenException;
import com.example.stomp.jwt.service.provider.JwtProvider;
import com.example.stomp.member.enum_type.MemberRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    // private final TokenRepositoy tokenRepository;

    // 엑세스 토큰 생성 메서드
    public String createAccessToken(CreateAccessTokenDto cTokenDto) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(JwtContants.ROLES_KEY, cTokenDto.stringAuthorities());
        claims.put(JwtContants.TYPE_DISCRIMINATOR_KEY, JwtContants.TokenType.ACCESS.toString());

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.accessTokenValidity());

        return jwtProvider.createToken(
                cTokenDto.email(),
                claims,
                Date.from(now),
                Date.from(expiry));
    }

    // 리프레시 토큰 생성 및 저장 메서드
    public CreateRefreshTokenResponse createAndSaveRefreshToken(CreateRefreshTokenDto cTokenDto) {
        String refreshToken;

        Map<String, Object> claims = new HashMap<>();

        claims.put(JwtContants.TYPE_DISCRIMINATOR_KEY,
                JwtContants.TokenType.REFRESH.toString());

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.accessTokenValidity());

        refreshToken = jwtProvider.createToken(cTokenDto.email(), claims, Date.from(now), Date.from(expiry));

        // tokenRepository.saveRefreshToken(
        // cTokenDto.email(), refreshToken,
        // Date.from(expiry));

        return new CreateRefreshTokenResponse(refreshToken, jwtProperties.refreshTokenValidity() / 1000);
    }

    // // 모든 토큰 무효화 메서드: 엑세스 토큰은 블랙리스트에 등록, 리프레쉬 토큰은 삭제
    // public void nullifyJwt(String accessToken) {
    // Claims claims = jwtProvider.parseClaims(accessToken);

    // tokenRepository.deleteRefreshToken(JwtContants.REFRESH_TOKEN_PREFIX +
    // claims.getSubject());

    // tokenRepository.blackAccessToken(JwtContants.BLACKLIST_PREFIX + accessToken,
    // JwtContants.BlackReason.LOGOUT.getValue(),
    // claims.getExpiration());
    // }

    // 토큰 검증 메서드
    public String validateToken(String token) {
        Claims claims;

        // AppException으로 throw.
        try {
            claims = jwtProvider.parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (SignatureException e) {
            throw new InvalidTokenException();
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            throw new InvalidTokenException();
        }

        // // 엑세스 토큰 재활용 방지.
        // if
        // (claims.get(JwtContants.TYPE_DISCRIMINATOR_KEY).equals(JwtContants.TokenType.ACCESS.getValue())
        // && tokenRepository.isBlacked(token)) {
        // throw new ExpiredTokenException();
        // }

        // // 리프레시 토큰 재활용 방지.
        // if
        // (claims.get(JwtContants.TYPE_DISCRIMINATOR_KEY).equals(JwtContants.TokenType.REFRESH.getValue())
        // && !tokenRepository.isRefreshTokenExist(JwtContants.REFRESH_TOKEN_PREFIX +
        // claims.getSubject())) {
        // throw new ExpiredTokenException();
        // }

        return claims.getSubject();
    }

    // // JwtFilter에서 엑세스 토큰으로 시큐리티 컨텍스트에 Authentication 넣을 때 사용!
    // public Authentication toAuthentication(String accessToken) {
    // Claims claims = jwtProvider.parseClaims(accessToken);

    // MemberPrincipal memberPrincipal =
    // MemberPrincipal.creatMemberPrincipalForSecurityContext(
    // (String) claims.getSubject(), // email
    // (String) claims.get(JwtContants.NICKNAME_KEY),
    // MemberRole
    // .valueOf(SecurityUtils.convertToAuthorities((String)
    // claims.get(JwtContants.ROLES_KEY))
    // .getFirst()
    // .getAuthority()),
    // accessToken);

    // return new UsernamePasswordAuthenticationToken(memberPrincipal, null,
    // memberPrincipal.getAuthorities());
    // }

}
