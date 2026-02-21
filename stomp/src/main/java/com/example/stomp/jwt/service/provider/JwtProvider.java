package com.example.stomp.jwt.service.provider;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final Key key;
    private final SignatureAlgorithm sigAlgorithm;

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String createToken(String subject, Map<String, Object> claims, Date issuedAt, Date expiration) {
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(key, sigAlgorithm)
                .compact();
    }

}
