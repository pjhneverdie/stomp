package com.example.stomp.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null) {

        }

        Optional.of(redisTemplate.opsForHash().entries(session.getId()))
                .filter(map -> !map.isEmpty())
                .ifPresentOrElse(map -> {
                    String auths = (String) map.get("authorities");
                    String memberId = (String) map.get("memberId");

                    long id = Long.parseLong(memberId);
                    List<SimpleGrantedAuthority> authorities = Arrays.stream(auths.split(","))
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    SimpleAuthenticationToken authentication = new SimpleAuthenticationToken(
                            new SimpleAuthenticationToken.SimpleMemberDetails(id, authorities));
                    SecurityContextHolder.createEmptyContext().setAuthentication(authentication);
                }, () -> {

                });
    }

}
