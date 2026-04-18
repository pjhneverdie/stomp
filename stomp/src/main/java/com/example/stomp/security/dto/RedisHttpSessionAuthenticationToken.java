package com.example.stomp.security.dto;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class RedisHttpSessionAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public RedisHttpSessionAuthenticationToken(RedisHttpSessionMemberPrincipal principal) {
        super(principal, null, principal.getAuthorities());
    }

    @Override
    public RedisHttpSessionMemberPrincipal getPrincipal() {
        return (RedisHttpSessionMemberPrincipal) super.getPrincipal();
    }

}
