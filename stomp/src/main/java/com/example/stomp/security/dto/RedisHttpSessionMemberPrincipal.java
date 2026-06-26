package com.example.stomp.security.dto;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;

import com.example.stomp.application.constant.SessionKeys;
import com.example.stomp.application.util.SecurityUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RedisHttpSessionMemberPrincipal {

    private final String id;

    private Collection<? extends GrantedAuthority> authorities;

    private final String httpSessionId;

    @Setter
    private String wsSessionId;

    public static RedisHttpSessionMemberPrincipal fromHashFields(Map<Object, Object> sessionMap) {
        Function<String, String> getStr = key -> (String) sessionMap.get(key);

        return new RedisHttpSessionMemberPrincipal(
                getStr.apply(SessionKeys.HFKEY_MEMBER_ID),
                SecurityUtil.stringToAuthorities(getStr.apply(SessionKeys.HFKEY_AUTHORITIES)),
                getStr.apply(SessionKeys.HFKEY_HTTP_SESSION_ID),
                getStr.apply(SessionKeys.HFKEY_WS_SESSION_ID));
    }

    public Long getLongId() {
        return Long.valueOf(id);
    }

}
