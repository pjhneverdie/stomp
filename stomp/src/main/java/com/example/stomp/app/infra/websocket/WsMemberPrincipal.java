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

    private Long memberId;

    @Setter
    private String roomUUID;

    @Setter
    private String nickname;

    @Setter
    private Boolean isRecon;

    public static WsMemberPrincipal create(RedisHttpSessionMemberPrincipal principal) {
        return new WsMemberPrincipal(Long.valueOf(principal.getId()), null, null, false);
    }

    @Override
    public String getName() {
        return String.valueOf(memberId);
    }

}
