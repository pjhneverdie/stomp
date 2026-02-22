package com.example.stomp.security.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.stomp.member.dto.JwtMemberDetails;
import com.example.stomp.security.config.SecurityConfig;
import com.example.stomp.shared.util.SecurityUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityContextFilter extends OncePerRequestFilter {

    final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return Arrays.stream(SecurityConfig.UNAUTHENTICATABLE_URL)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Long id = (Long) request.getAttribute(LoginFilter.MEMBER_ID_KEY);
        String roles = (String) request.getAttribute(LoginFilter.ROLES_KEY);

        JwtMemberDetails jwtMemberDetails = new JwtMemberDetails(id, SecurityUtil.stringToAuthorities(roles));

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(jwtMemberDetails, null,
                        jwtMemberDetails.getAuthorities()));

        filterChain.doFilter(request, response);
    }

}
