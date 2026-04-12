package com.example.stomp.chat.dto;

import java.security.Principal;

import org.springframework.security.core.Authentication;

import com.example.stomp.security.dto.SimpleAuthenticationToken;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SimpleWsPrincipal implements Principal {

    private final SimpleAuthenticationToken.SimpleMemberDetails simpleMemberDetails;
    
    @Setter
    private String roomId;

    public SimpleWsPrincipal(Authentication authentication) {
        this.simpleMemberDetails = (SimpleAuthenticationToken.SimpleMemberDetails) authentication.getPrincipal();
    }

    @Override
    public String getName() {
        return String.valueOf(simpleMemberDetails.memberId());
    }

}
