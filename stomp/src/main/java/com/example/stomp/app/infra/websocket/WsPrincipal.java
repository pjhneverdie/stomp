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
public class WsPrincipal implements Principal {

    private String memberId;

    private String memberCode;

    @Setter
    private String roomId;

    private String httpSessionId;

    public static WsPrincipal create(RedisHttpSessionMemberPrincipal memberDetails) {
        return new WsPrincipal(memberDetails.getId(), memberDetails.getCode(), memberDetails.getRoomId(),
                memberDetails.getSessionId());
    }

    @Override
    public String getName() {
        return this.memberId;
    }

}
