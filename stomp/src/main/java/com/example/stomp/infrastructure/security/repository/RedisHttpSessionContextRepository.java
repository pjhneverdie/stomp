package com.example.stomp.infrastructure.security.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import com.example.stomp.feature.member.dto.OidcMemberPrincipal;
import com.example.stomp.feature.trial.application.constant.SessionKeys;
import com.example.stomp.infrastructure.security.dto.RedisHttpSessionAuthenticationToken;
import com.example.stomp.infrastructure.security.dto.RedisHttpSessionMemberPrincipal;
import com.example.stomp.shared.util.CookieUtil;
import com.example.stomp.shared.util.SecurityUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisHttpSessionContextRepository implements SecurityContextRepository {

    private final StringRedisTemplate redis;

    public static final int SESSION_VALID_DAYS = 1;

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return CookieUtil.getLoginCookie(request)
                .map(Cookie::getValue)
                .map(sessionId -> SessionKeys.session(sessionId))
                .map(sessionKey -> Boolean.TRUE.equals(redis.hasKey(sessionKey)))
                .orElse(false);
    }

    public void setWsSessionId(String httpSessionId, String wsSessionId) {
        redis.opsForHash().put(SessionKeys.session(httpSessionId),
                SessionKeys.HFKEY_WS_SESSION_ID, wsSessionId);
    }

    public void deleteWsSessionId(String httpSessionId) {
        redis.opsForHash().delete(SessionKeys.session(httpSessionId),
                SessionKeys.HFKEY_WS_SESSION_ID);
    }

    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        Optional.ofNullable(context.getAuthentication())
                .filter(Authentication::isAuthenticated) // Make sure there are no negative specifics on authentication
                                                         // process.
                .ifPresent(authentication -> {
                    String sessionId = CookieUtil.getLoginCookie(request).map(Cookie::getValue)
                            .orElseGet(() -> UUID.randomUUID().toString()); // Create if absent.

                    OidcMemberPrincipal pc = (OidcMemberPrincipal) authentication.getPrincipal();

                    validateMultiSession(pc); // Reject if they are trying more than one device.

                    redis.executePipelined((RedisCallback<Object>) connection -> {
                        byte[] reverseKey = redis.getStringSerializer()
                                .serialize(SessionKeys.reverseIndex(pc.getId()));

                        byte[] sessionKey = redis.getStringSerializer()
                                .serialize(SessionKeys.session(sessionId));

                        connection.stringCommands().set(
                                reverseKey,
                                redis.getStringSerializer().serialize(sessionId));

                        Map<byte[], byte[]> hash = new HashMap<>();
                        hash.put(
                                redis.getStringSerializer().serialize(SessionKeys.HFKEY_MEMBER_ID),
                                redis.getStringSerializer().serialize(pc.getId()));
                        hash.put(
                                redis.getStringSerializer().serialize(SessionKeys.HFKEY_AUTHORITIES),
                                redis.getStringSerializer()
                                        .serialize(SecurityUtil.authoritiesToString(pc.getAuthorities())));
                        hash.put(
                                redis.getStringSerializer().serialize(SessionKeys.HFKEY_HTTP_SESSION_ID),
                                redis.getStringSerializer().serialize(sessionId));

                        connection.hashCommands().hMSet(sessionKey, hash);

                        return null;
                    });

                    CookieUtil.setLoginCookie(sessionId, response);
                });
    }

    private void validateMultiSession(OidcMemberPrincipal pc) {
        String oldId = redis.opsForValue().get(SessionKeys.reverseIndex(pc.getId()));

        if (oldId != null) {
            throw new AuthenticationServiceException("Please decline exisiting session.");
        }
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        return new RedisHttpSessionSecurityDefferedContext(
                () -> {
                    Optional<Cookie> cookieOpt = CookieUtil.getLoginCookie(request);
                    if (cookieOpt.isEmpty()) {
                        return null;
                    }

                    String sessionId = cookieOpt.get().getValue();
                    Optional<SecurityContext> scOpt = readSecurityContextFromRedis(sessionId);
                    if (scOpt.isEmpty()) {
                        return null;
                    }

                    redis.executePipelined((RedisCallback<Object>) connection -> {
                        byte[] reverseKey = redis.getStringSerializer()
                                .serialize(SessionKeys.reverseIndex(scOpt.get().getAuthentication().getName()));
                        byte[] sessionKey = redis.getStringSerializer().serialize(SessionKeys.session(sessionId));

                        connection.keyCommands().expire(sessionKey, TimeUnit.DAYS.toSeconds(SESSION_VALID_DAYS));
                        connection.keyCommands().expire(reverseKey, TimeUnit.DAYS.toSeconds(SESSION_VALID_DAYS));

                        return null;
                    });

                    return scOpt.get();
                },
                SecurityContextHolder.getContextHolderStrategy());
    }

    private Optional<SecurityContext> readSecurityContextFromRedis(String sessionId) {
        Map<Object, Object> hashFileds = redis.opsForHash().entries(SessionKeys.session(sessionId));

        if (hashFileds.isEmpty())
            return Optional.empty();

        SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();

        RedisHttpSessionAuthenticationToken at = new RedisHttpSessionAuthenticationToken(
                RedisHttpSessionMemberPrincipal.fromHashFields(hashFileds));

        sc.setAuthentication(at);

        return Optional.of(sc);
    }

    // We are using over Security 6.0.
    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return loadDeferredContext(requestResponseHolder.getRequest()).get();
    }

    // Slightly different version of SupplierDeferredSecurityContext.
    @RequiredArgsConstructor
    private static class RedisHttpSessionSecurityDefferedContext implements DeferredSecurityContext {

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
