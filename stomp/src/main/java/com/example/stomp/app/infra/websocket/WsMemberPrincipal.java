package com.example.stomp.app.infra.websocket;

import java.security.Principal;

import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class WsMemberPrincipal implements Principal {

    private String memberId;

    @Setter
    private String roomUUID;

    @Setter
    private Boolean isRecon;

    public static WsMemberPrincipal create(RedisHttpSessionMemberPrincipal principal) {
        return new WsMemberPrincipal(principal.getId(), null, false);
    }

    @Override
    public String getName() {
        return this.memberId;
    }

}
