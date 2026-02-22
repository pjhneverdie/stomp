package com.example.stomp.jwt.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.stomp.jwt.config.JwtContants;
import com.example.stomp.jwt.service.JwtService;
import com.example.stomp.member.dto.JwtMemberDetails;
import com.example.stomp.security.handler.SecurityExceptionHandler;
import com.example.stomp.shared.util.SecurityUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccessTokenValidationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final SecurityExceptionHandler securityExceptionHandler;

    public static final int BEARER_PREFIX_LENGTH = 7;
    public static final String ACCESS_TOKEN_HEADER_PREFIX = "Bearer ";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/auth/reissue");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(ACCESS_TOKEN_HEADER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.validateToken(authHeader.substring(BEARER_PREFIX_LENGTH));

            Long id = Long.parseLong(claims.getSubject());
            String roles = (String) claims.get(JwtContants.ROLES_KEY);

            JwtMemberDetails jwtMemberDetails = new JwtMemberDetails(id, SecurityUtil.stringToAuthorities(roles));

            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(jwtMemberDetails, null,
                            jwtMemberDetails.getAuthorities()));

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            securityExceptionHandler.handleFilterException(request, response, e);
        }
    }

}
