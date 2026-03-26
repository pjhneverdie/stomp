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
        return redisTemplate.opsForValue().get(request.getParameter(OAuth2ParameterNames.STATE));
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
            HttpServletResponse response) {
        redisTemplate.opsForValue().set(authorizationRequest.getState(), authorizationRequest,
                Duration.ofSeconds(60));

                
        System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
        System.out.println("저장완료!!!");
        System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
            HttpServletResponse response) {
        String state = request.getParameter(OAuth2ParameterNames.STATE);

        OAuth2AuthorizationRequest authRequest = redisTemplate.opsForValue().get(state);

        System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
        System.out.println(authRequest == null);
        System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");

        if (state != null) {
            redisTemplate.delete(state);
        }

        return authRequest;
    }

}
