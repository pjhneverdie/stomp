package com.example.stomp.member.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public record MemberDetails(
        String email,
        List<GrantedAuthority> authorities,
        OidcUser oidcUser) implements UserDetails, OidcUser {

    public static MemberDetails fromOidcUser(OidcUser oidcUser, List<GrantedAuthority> authorities) {
        return new MemberDetails(
                oidcUser.getEmail(),
                authorities,
                oidcUser);
    }

    public static MemberDetails fromJwt(String email, List<GrantedAuthority> authorities) {
        return new MemberDetails(
                email,
                authorities,
                null);
    }

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser != null ? oidcUser.getAttributes() : Collections.emptyMap();
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser != null ? oidcUser.getClaims() : Collections.emptyMap();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser != null ? oidcUser.getUserInfo() : null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser != null ? oidcUser.getIdToken() : null;
    }

}