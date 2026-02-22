package com.example.stomp.jwt.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.example.stomp.jwt.service.JwtService;
import com.example.stomp.security.handler.SecurityExceptionHandler;
import com.example.stomp.shared.util.CookieUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogoutFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final SecurityExceptionHandler securityExceptionHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        validateRefreshToken(request, response, filterChain);
        validateAccessToken(request, response, filterChain);
        filterChain.doFilter(request, response);
    }

    private void validateRefreshToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Cookie cookie = WebUtils.getCookie(request, CookieUtil.REFRESH_TOKEN_COOKIE_NAME);

        if (cookie != null) {
            try {
                jwtService.validateToken(cookie.getValue());
            } catch (Exception e) {
                securityExceptionHandler.handleFilterException(request, response, e);
            }
        } else {
            securityExceptionHandler.handleFilterException(request, response,
                    new IllegalArgumentException("this cookie does not contain refresh token"));
        }
    }

    private void validateAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(AccessTokenValidationFilter.ACCESS_TOKEN_HEADER_PREFIX)) {
            securityExceptionHandler.handleFilterException(request, response,
                    new IllegalArgumentException("this header does not contain access token"));
        }

        try {
            jwtService.validateToken(authHeader.substring(AccessTokenValidationFilter.BEARER_PREFIX_LENGTH));
        } catch (Exception e) {
            securityExceptionHandler.handleFilterException(request, response, e);
        }
    }
}
