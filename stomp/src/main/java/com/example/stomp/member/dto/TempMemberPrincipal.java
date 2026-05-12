package com.example.stomp.member.dto;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import lombok.Getter;

// OidcAuthorizationCodeAuthenticationProvider will set this as Principal.
@Getter
public class TempMemberPrincipal extends DefaultOidcUser { // This is used temporarily for making redis session.

    private final String id;

    public TempMemberPrincipal(
            List<GrantedAuthority> authorities,
            OidcIdToken idToken,
            OidcUserInfo userInfo,
            String id) {
        super(authorities, idToken, userInfo);

        this.id = id;
    }

}