package com.example.stomp.security.repository;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.util.CookieUtil;
import com.example.stomp.app.util.SecurityUtil;
import com.example.stomp.member.dto.TempMemberPrincipal;
import com.example.stomp.security.dto.RedisHttpSessionAuthenticationToken;
import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisHttpSessionContextRepository implements SecurityContextRepository {

    private final StringRedisTemplate redis;

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return CookieUtil.getLoginCookie(request)
                .map(Cookie::getValue)
                .map(sessionId -> SessionConstant.SESSION_HKEY_PREFIX + sessionId)
                .map(sessionKey -> Boolean.TRUE.equals(redis.hasKey(sessionKey)))
                .orElse(false);
    }

    public void setWsSessionId(String wsSessionId) {
    }

    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        Optional.ofNullable(context.getAuthentication())
                .filter(Authentication::isAuthenticated) // Make sure there are no negative specifics on authentication
                                                         // process.
                .ifPresent(at -> {
                    String sessionId = CookieUtil.getLoginCookie(request).map(Cookie::getValue)
                            .orElseGet(() -> UUID.randomUUID().toString()); // Create if absent.

                    TempMemberPrincipal pc = (TempMemberPrincipal) at.getPrincipal();

                    String luaScript = """
                            -- KEYS[1] : SESSION_HKEY_PREFIX
                            -- KEYS[2] : SESSION_REVERSE_INDEX_KEY_PREFIX
                            -- KEYS[3] : session expiration in seconds

                            -- ARGV[1] : newSessionId
                            -- ARGV[2] : memberId
                            -- ARGV[3] : authorities

                            -- 1. Delete previous session to comply with one session policy.
                            local sessionReverseIndexKey = KEYS[2] .. ARGV[2]
                            local oldSessionId = redis.call('GET', sessionReverseIndexKey)

                            -- If old session exists, we need to retrieve the wsSessionId before deleting it
                            local wsSessionId = nil
                            if oldSessionId then
                                local oldSessionKey = KEYS[1] .. oldSessionId
                                wsSessionId = redis.call('HGET', oldSessionKey, 'wsSessionId')
                                redis.call('DEL', oldSessionKey)
                            end

                            -- 2. Make the new session.
                            local newSessionKey = KEYS[1] .. ARGV[1]
                            redis.call('HMSET', newSessionKey,
                                'memberId', ARGV[2],
                                'authorities', ARGV[3],
                                'httpSessionId', ARGV[1],
                                'wsSessionId', wsSessionId
                            )

                            -- 3. Update the index and expiry.
                            redis.call('EXPIRE', newSessionKey, KEYS[3])
                            redis.call('SET', sessionReverseIndexKey, ARGV[1])
                            redis.call('EXPIRE', sessionReverseIndexKey, KEYS[3])

                            return;
                                                        """;

                    redis.execute(
                            new DefaultRedisScript<>(luaScript),
                            Arrays.asList(
                                    SessionConstant.SESSION_HKEY_PREFIX,
                                    SessionConstant.SESSION_REVERSE_INDEX_KEY_PREFIX,
                                    String.valueOf(TimeUnit.DAYS.toSeconds(SessionConstant.SESSION_VALID_DAYS))),
                            sessionId,
                            pc.getId(),
                            pc.getAuthorities());

                    // 이벤트 발생

                    CookieUtil.setLoginCookie(sessionId, response);
                });
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

                    SecurityContext sc = scOpt.get();

                    // Don't forget to extend session expiry.
                    extendSessionExpiry(sessionId, sc.getAuthentication().getName());

                    return sc;
                },
                SecurityContextHolder.getContextHolderStrategy());
    }

    private Optional<SecurityContext> readSecurityContextFromRedis(String sessionId) {
        Map<Object, Object> hashFileds = redis.opsForHash().entries(SessionConstant.SESSION_HKEY_PREFIX + sessionId);

        if (hashFileds.isEmpty())
            return Optional.empty();

        SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();

        RedisHttpSessionAuthenticationToken at = new RedisHttpSessionAuthenticationToken(
                RedisHttpSessionMemberPrincipal.fromHashFields(hashFileds));

        sc.setAuthentication(at);

        return Optional.of(sc);
    }

    private void extendSessionExpiry(String sessionId, String memberId) {
        redis.expire(SessionConstant.SESSION_HKEY_PREFIX + sessionId, 1, TimeUnit.DAYS);
        redis.expire(SessionConstant.SESSION_REVERSE_INDEX_KEY_PREFIX + memberId, 1, TimeUnit.DAYS);
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
