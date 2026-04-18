package com.example.stomp.security.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.context.HttpRequestResponseHolder;

import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.util.CookieUtil;
import com.example.stomp.app.util.SecurityUtil;
import com.example.stomp.member.dto.OidcMemberPrincipal;
import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;
import com.example.stomp.security.dto.RedisHttpSessionAuthenticationToken;

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
        String sessionId = CookieUtil.getCookie(request)
                .map(Cookie::getValue)
                .orElseGet(() -> UUID.randomUUID().toString()); // Create it if absence.

                OAuth2LoginAuthenticationFilter

        /**
         * @formatter:off
         * 
         * OAuth2LoginAuthenticationFilter(AbstractAuthenticationProcessingFilter) will call this method
         * when login succeed.
         * 
         * On this step, we have two to do.
         * 1. save user's info in redis like session.
         * 
         * @formatter:on
         */
        Optional.ofNullable(context.getAuthentication())
                .filter(Authentication::isAuthenticated)
                .ifPresent(at -> {
                    OidcMemberPrincipal pc = (OidcMemberPrincipal) at.getPrincipal();

                    getAndDeleteExisitingSession(pc.getId()).ifPresent((oldSessionId) -> {
                        // Publish the event notifying session switched.

                    });

                    Map<String, String> hashFields = Map.of(
                            SessionConstant.SESSION_MEMBER_ID_KEY, pc.getId(),
                            SessionConstant.SESSION_SESSION_ID_KEY, sessionId,
                            SessionConstant.SESSION_MEMBER_CODE_KEY, pc.getCode(),
                            SessionConstant.SESSION_AUHTORITIES_KEY, SecurityUtil.authoritiesToString(
                                    at.getAuthorities()));

                    saveSession(sessionId, hashFields);
                    createIndex(sessionId, pc.getId());
                    CookieUtil.setCookie(sessionId, response);
                });
    }

    private Optional<String> getAndDeleteExisitingSession(String memberId) {
        return Optional.ofNullable((String) redis.opsForValue()
                .getAndDelete(SessionConstant.MEMBER_SESSION_INDEX_PREFIX + memberId))
                .map(sessionId -> {
                    redis.delete(SessionConstant.SESSION_KEY_PREFIX + sessionId);
                    return sessionId;
                });
    }

    private void saveSession(String sessionId, Map<String, String> sessionMap) {
        String key = SessionConstant.SESSION_KEY_PREFIX + sessionId;

        redis.opsForHash().putAll(key, sessionMap);
        redis.expire(key, 1, TimeUnit.DAYS);
    }

    private void createIndex(String sessionId, String memberId) {
        String indexKey = SessionConstant.MEMBER_SESSION_INDEX_PREFIX + memberId;

        redis.opsForValue().set(indexKey, sessionId);
        redis.expire(indexKey, 1, TimeUnit.DAYS);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return CookieUtil.getCookie(request)
                .map(Cookie::getValue)
                .map(sessionId -> SessionConstant.SESSION_KEY_PREFIX + sessionId)
                .map(sessionKey -> Boolean.TRUE.equals(redis.hasKey(sessionKey)))
                .orElse(false);
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        return new RedisHttpSessionSecurityDefferedContext(() -> CookieUtil.getCookie(request)
                .map(cookie -> {
                    Optional<SecurityContext> scOptional = readSecurityContextFromRedis(cookie.getValue());

                    // Extend the Expiry of session.
                    scOptional.ifPresent((sc) -> {
                        extendExpiry(cookie.getValue(), sc.getAuthentication().getName());
                    });

                    // If sc is null, RedisHttpSessionSecurityDefferedContext will create it.
                    return scOptional.orElse(null);
                })
                /**
                 * @formatter:off
                 * 
                 * If cookie is null, return null supplier.
                 * Then RedisHttpSessionSecurityDefferedContext will create empty SecurityContext.
                 * 
                 * @formatter:on
                 */
                .orElse(null),
                SecurityContextHolder.getContextHolderStrategy());
    }

    private Optional<SecurityContext> readSecurityContextFromRedis(String sessionId) {
        Map<Object, Object> hashFileds = redis.opsForHash()
                .entries(SessionConstant.SESSION_KEY_PREFIX + sessionId);

        if (hashFileds.isEmpty())
            return Optional.empty();

        SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();

        RedisHttpSessionAuthenticationToken at = new RedisHttpSessionAuthenticationToken(
                RedisHttpSessionMemberPrincipal.fromHashFields(hashFileds));

        sc.setAuthentication(at);

        return Optional.of(sc);
    }

    private void extendExpiry(String sessionId, String memberId) {
        redis.expire(SessionConstant.SESSION_KEY_PREFIX + sessionId, 1, TimeUnit.DAYS);
        redis.expire(SessionConstant.MEMBER_SESSION_INDEX_PREFIX + memberId, 1, TimeUnit.DAYS);
    }

    // We are using over Security 6.0.
    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return loadDeferredContext(requestResponseHolder.getRequest()).get();
    }

    /**
     * @formatter:off
     * 
     * Slightly different version of SupplierDeferredSecurityContext used in SecurityContextRepository the library made.
     * 
     * @formatter:on
     */
    @RequiredArgsConstructor
    private static final class RedisHttpSessionSecurityDefferedContext implements DeferredSecurityContext {

        private final Supplier<SecurityContext> supplier;

        private final SecurityContextHolderStrategy strategy;

        private boolean missingContext;

        private SecurityContext securityContext;

        @Override
        public SecurityContext get() {
            init();
            return this.securityContext;
        }

        /**
         * @formatter:off
         * 
         * This method is used only in DelegatingSecurityContextRepository.
         * We don't have any relation with it under the case we use our custom SecurityContextRepository.
         * 
         * @formatter:on
         */
        @Override
        public boolean isGenerated() {
            init();
            return this.missingContext;
        }

        private void init() {
            if (this.securityContext != null) {
                return;
            }

            this.securityContext = this.supplier.get();
            this.missingContext = (this.securityContext == null);
            if (this.missingContext) {
                this.securityContext = this.strategy.createEmptyContext();
            }
        }

    }

}
