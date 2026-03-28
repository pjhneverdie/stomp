package com.example.stomp.security.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.util.SecurityUtil;
import com.example.stomp.member.dto.OidcMemberDetails;
import com.example.stomp.security.dto.SimpleAuthenticationToken;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisHttpSessionContextRepository implements SecurityContextRepository {

    private final RedisTemplate<String, Object> redis;

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        String sessionId = request.getSession(true).getId();
        Authentication authentication = context.getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        String memberId = String.valueOf(((OidcMemberDetails) authentication.getPrincipal()).getMemberId());
        if (memberId == null) {
            return;
        }

        // if user sign in with new device or browser
        // we're gonna nullify existing one
        nullifyExisitingSession(memberId);

        Map<String, String> sessionMap = new HashMap<>();
        sessionMap.put(SessionConstant.SESSION_MEMBER_ID_KEY, memberId);
        sessionMap.put(SessionConstant.SESSION_AUHTORITIES_KEY,
                SecurityUtil.authoritiesToString(authentication.getAuthorities()));

        saveSession(sessionId, sessionMap);
        createIndex(sessionId, memberId);
    }

    private void saveSession(String sessionId, Map<String, String> sessionMap) {
        String key = SessionConstant.SESSION_PREFIX + sessionId;

        redis.opsForHash().putAll(key, sessionMap);
        redis.expire(key, 1, TimeUnit.DAYS);
    }

    private void createIndex(String sessionId, String memberId) {
        String indexKey = SessionConstant.MEMBER_SESSION_INDEX_PREFIX + memberId;

        redis.opsForValue().set(indexKey, sessionId);
        redis.expire(indexKey, 1, TimeUnit.DAYS);
    }

    private void nullifyExisitingSession(String memberId) {
        String sessionId = (String) redis.opsForValue()
                .getAndDelete(SessionConstant.MEMBER_SESSION_INDEX_PREFIX + memberId);

        if (sessionId != null) {
            redis.delete(SessionConstant.SESSION_PREFIX + sessionId);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        return Boolean.TRUE.equals(redis.hasKey(SessionConstant.SESSION_PREFIX + session.getId()));
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        Supplier<SecurityContext> supplier = () -> readSecurityContextFromSession(request.getSession(false));

        String cookieHeader = request.getHeader("Cookie");
        System.out.println("원본 쿠키 헤더: " + cookieHeader);

        // 2. 톰캣이 파싱한 쿠키 목록
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                System.out.println("파싱된 쿠키 -> 이름: " + c.getName() + ", 값: " + c.getValue());
            }
        }

        return new DeferredSecurityContext() {
            @Override
            public SecurityContext get() {
                System.out.println("콘텍스트 가져왔어~");
                return supplier.get();
            }

            @Override
            public boolean isGenerated() {
                return false;
            }
        };
    }

    private SecurityContext readSecurityContextFromSession(HttpSession session) {
        SecurityContext sc = SecurityContextHolder.createEmptyContext();

        if (session == null)
            return sc;

        Authentication auth = createAuthentication(session);
        if (auth != null) {
            sc.setAuthentication(auth);
            extendExpiry(session.getId(),
                    ((SimpleAuthenticationToken.SimpleMemberDetails) auth.getPrincipal()).memberId());
        }

        return sc;
    }

    private Authentication createAuthentication(HttpSession session) {
        Map<Object, Object> sessionMap = redis.opsForHash().entries(SessionConstant.SESSION_PREFIX + session.getId());

        if (sessionMap.isEmpty())
            return null;

        String auths = (String) sessionMap.get(SessionConstant.SESSION_AUHTORITIES_KEY);
        String memberId = (String) sessionMap.get(SessionConstant.SESSION_MEMBER_ID_KEY);

        if (auths == null || memberId == null)
            return null;

        return new SimpleAuthenticationToken(
                new SimpleAuthenticationToken.SimpleMemberDetails(Long.parseLong(memberId),
                        SecurityUtil.stringToAuthorities(auths)));
    }

    private void extendExpiry(String sessionId, long memberId) {
        redis.expire(SessionConstant.SESSION_PREFIX + sessionId, 1, TimeUnit.DAYS);
        redis.expire(SessionConstant.MEMBER_SESSION_INDEX_PREFIX + memberId, 1, TimeUnit.DAYS);
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return loadDeferredContext(requestResponseHolder.getRequest()).get();
    }

}
