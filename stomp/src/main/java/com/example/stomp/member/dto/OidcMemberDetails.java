package com.example.stomp.member.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import com.example.stomp.member.domain.Member;

import lombok.Getter;

@Getter
public class OidcMemberDetails extends DefaultOidcUser {

    private final Member member;

    public OidcMemberDetails(
            Member member,
            OidcIdToken idToken,
            OidcUserInfo userInfo) {
        super(member.getAuthorities(), idToken, userInfo);
        this.member = member;
    }

}