package com.example.stomp.security.dto;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.util.SecurityUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RedisHttpSessionMemberPrincipal implements Principal {

    private String sessionId;
    private String id;
    private String code;
    private Collection<? extends GrantedAuthority> authorities;

    @Setter
    private String roomId;

    public static RedisHttpSessionMemberPrincipal fromHashFields(Map<Object, Object> sessionMap) {
        Function<String, String> getStr = key -> (String) sessionMap.get(key);

        return new RedisHttpSessionMemberPrincipal(
                getStr.apply(SessionConstant.SESSION_SESSION_ID_KEY),
                getStr.apply(SessionConstant.SESSION_MEMBER_ID_KEY),
                getStr.apply(SessionConstant.SESSION_MEMBER_CODE_KEY),
                SecurityUtil.stringToAuthorities(getStr.apply(SessionConstant.SESSION_AUHTORITIES_KEY)),
                getStr.apply(SessionConstant.SESSION_ROOM_ID_KEY));

    }

    @Override
    public String getName() {
        return this.id;
    }

}
