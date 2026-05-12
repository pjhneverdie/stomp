package com.example.stomp.app.infra.websocket;

import java.security.Principal;

import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class WsMemberPrincipal implements Principal {

    private final Long id;

    @Setter
    private String roomUUID;

    @Setter
    private String nickname;

    @Setter
    private Boolean isRecon;

    private String wsSessionId;

    private String httpSessionId;

    public static WsMemberPrincipal create(RedisHttpSessionMemberPrincipal principal) {
        return new WsMemberPrincipal(Long.valueOf(principal.getId()), null, null, null, null, null);
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

}
