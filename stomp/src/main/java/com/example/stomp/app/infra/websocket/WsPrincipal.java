package com.example.stomp.app.infra.websocket;

import java.security.Principal;

import com.example.stomp.security.dto.HttpSessionMemberDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WsPrincipal implements Principal {

    private final long memberId;
    private final String memberCode;

    @Setter
    private String roomId;

    private final String httpSessionId;

    public WsPrincipal(HttpSessionMemberDetails memberDetails) {
        this.memberId = memberDetails.getId();
        this.memberCode = memberDetails.getCode();
        this.httpSessionId = memberDetails.getSessionId();
    }

    @Override
    public String getName() {
        return Long.toString(this.memberId);
    }

}
