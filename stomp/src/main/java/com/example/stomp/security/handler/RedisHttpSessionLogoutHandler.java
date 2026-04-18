package com.example.stomp.security.handler;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.util.CookieUtil;
import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisHttpSessionLogoutHandler implements LogoutHandler {

    private final RedisTemplate<String, Object> redis;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CookieUtil.getLoginCookie(request)
                .map(Cookie::getValue).ifPresent((sessionId) -> {
                    String memberId = ((RedisHttpSessionMemberPrincipal) authentication
                            .getPrincipal()).getId();

                    redis.delete(SessionConstant.SESSION_KEY_PREFIX + sessionId);
                    redis.delete(SessionConstant.MEMBER_SESSION_INDEX_KEY_PREFIX
                            + memberId);
                });
    }

}
