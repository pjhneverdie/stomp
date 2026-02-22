package com.example.stomp.security.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.stomp.jwt.dto.exception.AccessTokenHeaderValueDoesNotExistException;
import com.example.stomp.jwt.service.JwtService;
import com.example.stomp.security.config.SecurityConfig;
import com.example.stomp.security.handler.SecurityExceptionHandler;
import com.example.stomp.shared.util.ClaimUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityExceptionHandler securityExceptionHandler;

    public static final int BEARER_PREFIX_LENGTH = 7;
    public static final String ACCESS_TOKEN_HEADER_PREFIX = "Bearer ";
    public static final String MEMBER_ID_KEY = "memberId";
    public static final String ROLES_KEY = "roles";

    final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(SecurityConfig.LOGIN_FILTER_WHITE_LIST)
                .anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(ACCESS_TOKEN_HEADER_PREFIX)) {
            securityExceptionHandler.handleFilterException(request, response,
                    new AccessTokenHeaderValueDoesNotExistException());

            return;
        }

        try {
            Claims claims = jwtService.validateToken(authHeader.substring(BEARER_PREFIX_LENGTH));
            request.setAttribute(MEMBER_ID_KEY, ClaimUtil.getMemberId(claims));
            request.setAttribute(ROLES_KEY, ClaimUtil.getRoles(claims));

            // once validation complete
            // setting SecurityContext is SecurityContextFilter's role
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            securityExceptionHandler.handleFilterException(request, response, e);
        }
    }

}
