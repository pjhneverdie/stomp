package com.example.stomp.member.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.example.stomp.member.domain.Member;
import com.example.stomp.member.dto.TempMemberPrincipal;
import com.example.stomp.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService extends OidcUserService {

        private final MemberRepository memberRepository;

        @Transactional
        @Override
        public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
                OidcUser oidcUser = super.loadUser(userRequest);

                return memberRepository.fetchJoinByEmailWithParticipatedRooms(oidcUser.getEmail())
                                .map(member -> {
                                        member.login(oidcUser.getEmail(), oidcUser.getPicture());

                                        return new TempMemberPrincipal(
                                                        member.getAuthorities(),
                                                        oidcUser.getIdToken(),
                                                        oidcUser.getUserInfo(),
                                                        String.valueOf(member.getId()));
                                })
                                .orElseGet(() -> {
                                        Member member = memberRepository.save(Member.createMember(oidcUser.getEmail(),
                                                        oidcUser.getPicture()));

                                        return new TempMemberPrincipal(
                                                        member.getAuthorities(),
                                                        oidcUser.getIdToken(),
                                                        oidcUser.getUserInfo(),
                                                        String.valueOf(member.getId()));
                                });
        }

}
