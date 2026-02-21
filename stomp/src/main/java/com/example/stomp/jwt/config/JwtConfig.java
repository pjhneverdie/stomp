package com.example.stomp.jwt.config;

import java.security.Key;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtConfig {

    @Bean
    public Key jwtSigningKey(JwtProperties jwtProperties) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret()));
    }

    @Bean
    public SignatureAlgorithm signatureAlgorithm() {
        return SignatureAlgorithm.HS256;
    }

}
