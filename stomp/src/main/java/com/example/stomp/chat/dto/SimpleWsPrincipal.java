package com.example.stomp.chat.dto;

import java.security.Principal;

import org.springframework.security.core.Authentication;

import com.example.stomp.security.dto.SimpleAuthenticationToken;

public class SimpleWsPrincipal implements Principal {

    private final SimpleAuthenticationToken.SimpleMemberDetails simpleMemberDetails;

    public SimpleWsPrincipal(Authentication authentication) {
        this.simpleMemberDetails = (SimpleAuthenticationToken.SimpleMemberDetails) authentication.getPrincipal();
    }

    @Override
    public String getName() {
        return String.valueOf(simpleMemberDetails.memberId());
    }

}
