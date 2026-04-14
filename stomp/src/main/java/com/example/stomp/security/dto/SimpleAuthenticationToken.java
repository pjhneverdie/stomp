package com.example.stomp.security.dto;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class SimpleAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public SimpleAuthenticationToken(SimpleMemberDetails principal) {
        super(principal, null, principal.authorities());
    }

    public record SimpleMemberDetails(long memberId, String sessionId, String code,
            Collection<? extends GrantedAuthority> authorities, String roomId) {
    }

}
