package com.example.stomp.security.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MemberDetails {

    private final long memberId;
    private final String sessionId;
    private final String code;
    private final Collection<? extends GrantedAuthority> authorities;

    @Setter
    private String roomId;

}
