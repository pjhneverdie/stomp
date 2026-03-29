package com.example.stomp.security.repository;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisHttpSessionContextRepository implements SecurityContextRepository {

    private final RedisTemplate<String, Object> redis;

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        String sessionId = Optional.ofNullable(getCookie(request))
                .map(Cookie::getValue)
                .orElseGet(() -> UUID.randomUUID().toString());

        Optional.ofNullable(context.getAuthentication())
                .filter(Authentication::isAuthenticated)
                .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken))
                .ifPresent(authentication -> {
                    Optional.ofNullable(
                            String.valueOf(((OidcMemberDetails) authentication.getPrincipal()).getMemberId()))
                            .ifPresent((memberId) -> {
                                nullifyExisitingSession(memberId);

                                Map<String, String> sessionMap = Map.of(
                                        SessionConstant.SESSION_MEMBER_ID_KEY, memberId,
                                        SessionConstant.SESSION_AUHTORITIES_KEY,
                                        SecurityUtil.authoritiesToString(authentication.getAuthorities()));

                                saveSession(sessionId, sessionMap);
                                createIndex(sessionId, memberId);
                                setCookie(sessionId, response);
                            });
                });
    }

    private void nullifyExisitingSession(String memberId) {
        Optional.ofNullable((String) redis.opsForValue()
                .getAndDelete(SessionConstant.MEMBER_SESSION_INDEX_PREFIX + memberId)).ifPresent((sessionId) -> {
                    redis.delete(SessionConstant.SESSION_PREFIX + sessionId);
                });
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

    private void setCookie(String sessionId, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(SessionConstant.COOKIE_NAME, sessionId)
                .secure(true)
                .httpOnly(true)
                .maxAge(Duration.ofDays(1))
                .sameSite(SameSite.LAX.toString())
                .path(SessionConstant.COOKIE_PATH)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return Optional.ofNullable(getCookie(request))
                .map(Cookie::getValue)
                .map(sessionId -> SessionConstant.SESSION_PREFIX + sessionId)
                .map(redisKey -> Boolean.TRUE.equals(redis.hasKey(redisKey)))
                .orElse(false);
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        Supplier<SecurityContext> supplier = () -> readSecurityContextFromCookie(getCookie(request));

        return new DeferredSecurityContext() {
            @Override
            public SecurityContext get() {
                return supplier.get();
            }

            @Override
            public boolean isGenerated() {
                return false;
            }
        };
    }

    private SecurityContext readSecurityContextFromCookie(Cookie cookie) {
        SecurityContext sc = SecurityContextHolder.createEmptyContext();

        Optional.ofNullable(cookie)
                .map(this::createAuthentication)
                .ifPresent(auth -> {
                    sc.setAuthentication(auth);

                    if (auth.getPrincipal() instanceof SimpleAuthenticationToken.SimpleMemberDetails details) {
                        extendExpiry(cookie.getValue(), details.memberId());
                    }
                });

        return sc;
    }

    private Authentication createAuthentication(Cookie cookie) {
        return Optional.ofNullable(cookie)
                .map(Cookie::getValue)
                .map(sessionId -> redis.opsForHash().entries(SessionConstant.SESSION_PREFIX + sessionId))
                .filter(map -> !map.isEmpty())
                .map(map -> {
                    String auths = (String) map.get(SessionConstant.SESSION_AUHTORITIES_KEY);
                    String memberId = (String) map.get(SessionConstant.SESSION_MEMBER_ID_KEY);

                    if (auths == null || memberId == null)
                        return null;

                    return new SimpleAuthenticationToken(
                            new SimpleAuthenticationToken.SimpleMemberDetails(
                                    Long.parseLong(memberId),
                                    SecurityUtil.stringToAuthorities(auths)));
                })
                .orElse(null);
    }

    private void extendExpiry(String sessionId, long memberId) {
        redis.expire(SessionConstant.SESSION_PREFIX + sessionId, 1, TimeUnit.DAYS);
        redis.expire(SessionConstant.MEMBER_SESSION_INDEX_PREFIX + memberId, 1, TimeUnit.DAYS);
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return loadDeferredContext(requestResponseHolder.getRequest()).get();
    }

    private Cookie getCookie(HttpServletRequest request) {
        return Stream.ofNullable(request.getCookies())
                .flatMap(Arrays::stream)
                .filter(c -> SessionConstant.COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .orElse(null);
    }

}
