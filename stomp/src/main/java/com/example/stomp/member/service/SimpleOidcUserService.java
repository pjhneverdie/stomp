package com.example.stomp.member.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import com.example.stomp.member.domain.Member;
import com.example.stomp.member.dto.OidcMemberDetails;
import com.example.stomp.member.repository.MemberRepository;
import com.example.stomp.shared.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SimpleOidcUserService extends OidcUserService {

    private final MemberRepository memberRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        Member member = memberRepository.findByEmail(oidcUser.getEmail())
                .orElseGet(
                        () -> memberRepository.save(Member.createMember(oidcUser.getEmail(), oidcUser.getPicture())));

        List<GrantedAuthority> authorities = SecurityUtil.stringToAuthorities(member.getMemberRole().toString());
        return new OidcMemberDetails(member.getId(), authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

}
