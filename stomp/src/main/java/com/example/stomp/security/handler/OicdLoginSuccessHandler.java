package com.example.stomp.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.dto.ApiResponse;
import com.example.stomp.app.util.SecurityUtil;
import com.example.stomp.member.dto.OidcMemberDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OicdLoginSuccessHandler implements AuthenticationSuccessHandler {
        private final ObjectMapper objectMapper;
        private final RedisTemplate<String, Object> redisTemplate;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                nullifyExisitingSession();
                setSession(request, authentication);

                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());

                objectMapper.writeValue(response.getWriter(), ApiResponse.createEmptySuccessResponse());
        }

        private void nullifyExisitingSession() {
                // 이미 세션 있으면
                // 기존 세션 무효화
        }

        private void setSession(HttpServletRequest request, Authentication authentication) {
                HttpSession session = request.getSession(true);

                String sessionId = session.getId();
                String memberId = String.valueOf(((OidcMemberDetails) authentication.getPrincipal()).getMemberId());

                Map<String, String> sessionMap = new HashMap<>();
                sessionMap.put(SessionConstant.SESSION_MEMBER_ID_KEY, memberId);
                sessionMap.put(SessionConstant.SESSION_AUHTORITIES_KEY,
                                SecurityUtil.authoritiesToString(authentication.getAuthorities()));

                String key = SessionConstant.SESSION_PREFIX + sessionId;
                redisTemplate.opsForHash().putAll(key, sessionMap);
                redisTemplate.expire(key, 30, TimeUnit.MINUTES);

                String indexKey = SessionConstant.MEMBER_SESSION_INDEX_PREFIX + memberId;
                redisTemplate.opsForValue().set(indexKey, sessionId);
                redisTemplate.expire(indexKey, 30, TimeUnit.MINUTES);
        }

}
