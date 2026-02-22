package com.example.stomp.security.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.example.stomp.jwt.dto.exception.AccessTokenHeaderValueDoesNotExistException;
import com.example.stomp.jwt.dto.exception.RefreshTokenCookieDoesNotExistException;
import com.example.stomp.jwt.service.JwtService;
import com.example.stomp.security.config.SecurityConfig;
import com.example.stomp.security.handler.SecurityExceptionHandler;
import com.example.stomp.shared.util.ClaimUtil;
import com.example.stomp.shared.util.CookieUtil;

import io.jsonwebtoken.Claims;
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
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals(SecurityConfig.LOGOUT_URL);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isValidRefreshToken(request, response) && isValidAccessToken(request, response)) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isValidRefreshToken(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cookie cookie = WebUtils.getCookie(request, CookieUtil.REFRESH_TOKEN_COOKIE_NAME);

        if (cookie == null) {
            securityExceptionHandler.handleFilterException(request, response,
                    new RefreshTokenCookieDoesNotExistException());

            return false;
        }

        try {
            jwtService.validateToken(cookie.getValue());
            return true;
        } catch (Exception e) {
            securityExceptionHandler.handleFilterException(request, response, e);
            return false;
        }
    }

    private boolean isValidAccessToken(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(LoginFilter.ACCESS_TOKEN_HEADER_PREFIX)) {
            securityExceptionHandler.handleFilterException(request, response,
                    new AccessTokenHeaderValueDoesNotExistException());

            return false;
        }

        try {
            Claims claims = jwtService.validateToken(authHeader.substring(LoginFilter.BEARER_PREFIX_LENGTH));
            request.setAttribute(LoginFilter.MEMBER_ID_KEY, ClaimUtil.getMemberId(claims));
            request.setAttribute(LoginFilter.ROLES_KEY, ClaimUtil.getRoles(claims));

            return true;
        } catch (Exception e) {
            securityExceptionHandler.handleFilterException(request, response, e);
            return false;
        }
    }

}
