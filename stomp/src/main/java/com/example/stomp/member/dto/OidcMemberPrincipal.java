package com.example.stomp.member.dto;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import com.example.stomp.member.domain.Member;

import lombok.Getter;

// OidcAuthorizationCodeAuthenticationProvider will set this as Principal.
// This is used temporarily between when login succeed and making session.
@Getter
public class OidcMemberPrincipal extends DefaultOidcUser {

    private final String id;
    private final String code;
    private final String roomId;

    public OidcMemberPrincipal(
            Member member,
            OidcIdToken idToken,
            OidcUserInfo userInfo) {

        super(member.getAuthorities(), idToken, userInfo);
        this.id = String.valueOf(member.getId());
        this.code = member.getCode();
        this.roomId = (member.getParticipatedRoom() != null)
                ? String.valueOf(member.getParticipatedRoom().getId())
                : null;
    }

}