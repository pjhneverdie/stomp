package com.example.stomp.security.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class HttpSessionMemberDetails {

    private final long id;
    private final String code;
    private final Collection<? extends GrantedAuthority> authorities;

    @Setter
    private String roomId;

    private final String sessionId;

}
