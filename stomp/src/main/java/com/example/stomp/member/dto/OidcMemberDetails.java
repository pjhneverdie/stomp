package com.example.stomp.member.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class OidcMemberDetails extends DefaultOidcUser {

    private final long memberId;

    @JsonCreator
    public OidcMemberDetails(
            @JsonProperty("memberId") long memberId,
            @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
            @JsonProperty("idToken") OidcIdToken idToken,
            @JsonProperty("userInfo") OidcUserInfo userInfo) {
        super(authorities, idToken, userInfo);
        this.memberId = memberId;
    }

}