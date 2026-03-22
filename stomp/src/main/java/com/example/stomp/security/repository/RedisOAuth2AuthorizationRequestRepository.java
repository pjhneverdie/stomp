package com.example.stomp.security.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final RedisTemplate<String, OAuth2AuthorizationRequest> redisTemplate;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        System.out.println("bro 1");
        System.out.println("bro 1");
        System.out.println("bro 1");
        System.out.println("bro 1");
        return redisTemplate.opsForValue().get(request.getParameter(OAuth2ParameterNames.STATE));
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
            HttpServletResponse response) {
        System.out.println("bro 2");
        System.out.println("bro 2");
        System.out.println("bro 2");
        System.out.println("bro 2");
        redisTemplate.opsForValue().set(authorizationRequest.getState(), authorizationRequest, Duration.ofSeconds(2));
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
            HttpServletResponse response) {
        System.out.println("bro 3");
        System.out.println("bro 3");
        System.out.println("bro 3");
        System.out.println("bro 3");
        String state = request.getParameter(OAuth2ParameterNames.STATE);

        OAuth2AuthorizationRequest authRequest = redisTemplate.opsForValue().get(state);

        if (state != null) {
            redisTemplate.delete(state);
        }

        return authRequest;
    }

}
