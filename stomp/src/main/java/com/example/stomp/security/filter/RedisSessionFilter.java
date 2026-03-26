package com.example.stomp.security.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.util.SecurityUtil;
import com.example.stomp.security.dto.SimpleAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisSessionFilter extends OncePerRequestFilter {
        private final RedisTemplate<String, Object> redis;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {
                HttpSession session = request.getSession(false);

                if (session == null) {
                        filterChain.doFilter(request, response);
                } else {
                        SecurityContextHolder.getContext().setAuthentication(createAuthentication(session));
                }
        }

        private Authentication createAuthentication(HttpSession session) {
                Map<Object, Object> sessionMap = redis.opsForHash().entries(session.getId());

                String auths = (String) sessionMap.get(SessionConstant.SESSION_AUHTORITIES_KEY);
                String memberId = (String) sessionMap.get(SessionConstant.SESSION_MEMBER_ID_KEY);

                return new SimpleAuthenticationToken(
                                new SimpleAuthenticationToken.SimpleMemberDetails(Long.parseLong(memberId),
                                                SecurityUtil.stringToAuthorities(auths)));
        }

}
