package com.example.stomp.infrastructure.security.handler;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.example.stomp.feature.trial.application.constant.SessionKeys;
import com.example.stomp.infrastructure.security.dto.RedisHttpSessionMemberPrincipal;
import com.example.stomp.shared.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisHttpSessionLogoutHandler implements LogoutHandler {

    private final StringRedisTemplate redis;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CookieUtil.getLoginCookie(request)
                .map(Cookie::getValue).ifPresent((sessionId) -> {
                    String memberId = ((RedisHttpSessionMemberPrincipal) authentication
                            .getPrincipal()).getId();

                    redis.delete(List.of(
                            SessionKeys.session(sessionId),
                            SessionKeys.reverseIndex(memberId)));
                });
    }

}
