package com.example.stomp.member.service;

import java.util.UUID;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.example.stomp.member.domain.Member;
import com.example.stomp.member.dto.OidcMemberPrincipal;
import com.example.stomp.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OidcMemberService extends OidcUserService {

        private final MemberRepository memberRepository;

        @Override
        public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
                OidcUser oidcUser = super.loadUser(userRequest);

                Member member = memberRepository.findByEmailWithRoom(oidcUser.getEmail())
                                .orElseGet(() -> memberRepository.save(Member.createMember(
                                                oidcUser.getEmail(),
                                                oidcUser.getPicture(),
                                                UUID.randomUUID().toString())));

                return new OidcMemberPrincipal(member, oidcUser.getIdToken(), oidcUser.getUserInfo());
        }

}
