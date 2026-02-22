package com.example.stomp.jwt.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.stomp.jwt.config.JwtContants;
import com.example.stomp.jwt.config.JwtContants.TokenType;
import com.example.stomp.jwt.dto.requestscope.JwtContext;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtContextFilter extends OncePerRequestFilter {
    private final JwtContext jwtContext;
    public static final String CLAIMS_KEY = "claims";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Claims claims = (Claims) request.getAttribute(CLAIMS_KEY);

        JwtContants.TokenType tokenType = TokenType
                .valueOf((String) request.getAttribute(JwtContants.TYPE_DISCRIMINATOR_KEY));

        // once token validated, you don't need to parse again
        switch (tokenType) {
            case ACCESS:
                jwtContext.setAccessTokenClaims(claims);
                break;

            case REFRESH:
                jwtContext.setRefreshTokenClaims(claims);
                break;
        }

        filterChain.doFilter(request, response);
    }

}
