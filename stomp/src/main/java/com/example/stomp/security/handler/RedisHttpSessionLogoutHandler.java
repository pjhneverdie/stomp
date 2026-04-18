package com.example.stomp.security.handler;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.security.dto.RedisHttpSessionAuthenticationToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisHttpSessionLogoutHandler implements LogoutHandler {

    private final RedisTemplate<String, Object> redis;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            redis.delete(SessionConstant.SESSION_KEY_PREFIX + session.getId());
        }

        if (authentication != null) {
            redis.delete(SessionConstant.MEMBER_SESSION_INDEX_PREFIX
                    + ((RedisHttpSessionAuthenticationToken.SimpleMemberDetails) authentication
                            .getPrincipal()).memberId());
        }
    }

}
