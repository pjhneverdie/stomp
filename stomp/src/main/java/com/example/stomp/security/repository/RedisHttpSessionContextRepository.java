package com.example.stomp.security.repository;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextHolderFilter;
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
    public boolean containsContext(HttpServletRequest request) {
        return CookieUtil.getLoginCookie(request)
                .map(Cookie::getValue)
                .map(sessionId -> SessionConstant.SESSION_KEY_PREFIX + sessionId)
                .map(sessionKey -> Boolean.TRUE.equals(redis.hasKey(sessionKey)))
                .orElse(false);
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        /**
         * @formatter:off
         * 
         * OAuth2LoginAuthenticationFilter(AbstractAuthenticationProcessingFilter) will call this method
         * when login succeed.
         * 
         * On this step, we have two to do.
         * 
         * 1. save the user's info in redis like session.
         * 2. naturally switch session rather than blocking the new login try if the user logins with another paths
         *    such as changing device.
         * 
         * @formatter:on
         */
        Optional.ofNullable(context.getAuthentication())
                .filter(Authentication::isAuthenticated) // Make sure there are no negative specifics on authentication
                                                         // process.
                .ifPresent(at -> {
                    String sessionId = CookieUtil.getLoginCookie(request)
                            .map(Cookie::getValue)
                            .orElseGet(() -> UUID.randomUUID().toString()); // Create if absent.

                    OidcMemberPrincipal pc = (OidcMemberPrincipal) at.getPrincipal();

                    String participatedRoomId = pc.getRoomId();

                    /**
                     * @formatter:off
                     * 
                     * If a user logined, but there is ongoing chat, this usually means a device was switched.
                     * Or it is possible as well that a user cleared brower caches.
                     * 
                     * ANYWAY, we have a response to make user can continue the chat. Save the 'roomId' in session. 
                     * 
                     * @formatter:on
                     */
                    String luaScript = """
                                --[[
                                    I wondered if any user would actually attempt simultaneous login try.

                                    Trying to cover every possiblescenario would make the app's complexity spiral out of control.

                                    However, I’ve decided to push the level of detail as far as I can personally handle. Let's go.
                                --]]
                                -- KEYS[1] : SESSION_KEY_PREFIX
                                -- KEYS[2] : MEMBER_SESSION_INDEX_KEY_PREFIX
                                -- KEYS[3] : session expiration in seconds

                                -- ARGV[1] : newSessionId
                                -- ARGV[2] : memberId
                                -- ARGV[3] : memberCode
                                -- ARGV[4] : authorities
                                -- ARGV[5] : roomId (nullable)

                                -- 1. delete previous session to comply one session policy.
                                local memberIndexKey = KEYS[2] .. ARGV[2]
                                local oldSessionId = redis.call('GET', memberIndexKey)

                                if oldSessionId then
                                    local oldSessionKey = KEYS[1] .. oldSessionId
                                    redis.call('DEL', oldSessionKey)
                                end

                                -- 2. make the new session.
                                local newSessionKey = KEYS[1] .. ARGV[1]
                                redis.call('HMSET', newSessionKey,
                                    'memberId', ARGV[2],
                                    'sessionId', ARGV[1],
                                    'memberCode', ARGV[3],
                                    'authorities', ARGV[4]
                                )

                                -- 3. add roomId field if exists.
                                if ARGV[5] and ARGV[5] ~= "" then
                                    redis.call('HSET', newSessionKey, 'roomId', ARGV[5])
                                end

                                -- 4. update the index and expiry.
                                redis.call('EXPIRE', newSessionKey, KEYS[3])
                                redis.call('SET', memberIndexKey, ARGV[1])
                                redis.call('EXPIRE', memberIndexKey, KEYS[3])

                                return;
                            """;

                    redis.execute(
                            new DefaultRedisScript<>(luaScript),
                            Arrays.asList(
                                    SessionConstant.SESSION_KEY_PREFIX,
                                    SessionConstant.MEMBER_SESSION_INDEX_KEY_PREFIX,
                                    String.valueOf(TimeUnit.DAYS.toSeconds(SessionConstant.SESSION_VALID_DAYS))),
                            sessionId,
                            pc.getId(),
                            pc.getCode(),
                            SecurityUtil.authoritiesToString(at.getAuthorities()),
                            participatedRoomId != null ? participatedRoomId : "");

                    if (participatedRoomId != null) {
                        /**
                         * @formatter:off
                         * Our quest doesn't end. You know that WebSocket still remains regardless of deletion of HttpSession.
                         * We should make it disconnected as well.
                         * 
                         * The problem is, in here, Since we got multiple servers, We can't be sure whether the server which the user requests for login 
                         * is the server which manages the WebSocket connection of the user's previous device. 
                         * 
                         * For this reason, we have to publish an event to all of the servers in order to cut them off.
                         * @formatter:on 
                         */

                        // ..... event realted codes...
                    }

                    CookieUtil.setLoginCookie(sessionId, response);
                });
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        System.out.println("실행은했어");
        return new RedisHttpSessionSecurityDefferedContext(
                () -> {
                    Optional<Cookie> cookieOpt = CookieUtil.getLoginCookie(request);

                    if (cookieOpt.isEmpty()) {
                        System.out.println("없어요;");
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
        String d = SessionConstant.SESSION_KEY_PREFIX + sessionId;
    
        // "" 이슈
        // null 아니라 "" 이슈
        Map<Object, Object> hashFileds = redis.opsForHash().entries(SessionConstant.SESSION_KEY_PREFIX + sessionId);

        if (hashFileds.isEmpty())
            return Optional.empty();

        System.out.println("있어요;");
        SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();

        RedisHttpSessionAuthenticationToken at = new RedisHttpSessionAuthenticationToken(
                RedisHttpSessionMemberPrincipal.fromHashFields(hashFileds));

        sc.setAuthentication(at);

        return Optional.of(sc);
    }

    private void extendSessionExpiry(String sessionId, String memberId) {
        redis.expire(SessionConstant.SESSION_KEY_PREFIX + sessionId, 1, TimeUnit.DAYS);
        redis.expire(SessionConstant.MEMBER_SESSION_INDEX_KEY_PREFIX + memberId, 1, TimeUnit.DAYS);
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
