package com.example.stomp.feature.member.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stomp.feature.member.domain.Member;
import com.example.stomp.feature.member.dto.MemberExceptions;
import com.example.stomp.feature.member.dto.OidcMemberPrincipal;
import com.example.stomp.feature.member.repository.MemberRepository;
import com.example.stomp.shared.exception.AppException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService extends OidcUserService {

        private final MemberRepository memberRepository;

        @Transactional
        @Override
        public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
                OidcUser oidcUser = super.loadUser(userRequest);

                return memberRepository.findByEmail(oidcUser.getEmail())
                                .map(member -> {
                                        // Keep tracking the diffs.
                                        member.login(oidcUser.getEmail(), oidcUser.getPicture());

                                        return new OidcMemberPrincipal(
                                                        member.getAuthorities(),
                                                        oidcUser.getIdToken(),
                                                        oidcUser.getUserInfo(),
                                                        String.valueOf(member.getId()));
                                })
                                .orElseGet(() -> {
                                        Member member = memberRepository.save(Member.create(oidcUser.getEmail(),
                                                        oidcUser.getPicture()));

                                        return new OidcMemberPrincipal(
                                                        member.getAuthorities(),
                                                        oidcUser.getIdToken(),
                                                        oidcUser.getUserInfo(),
                                                        String.valueOf(member.getId()));
                                });
        }

        public Member findById(Long id) {
                return memberRepository.findById(id).orElseThrow(() -> {
                        throw new AppException(MemberExceptions.UNEXISTS_MEMBER);
                });
        }

        public Member getMemberWithCredentialById(Long id) {
                return memberRepository.findWithCredentialById(id).orElseThrow(() -> {
                        throw new AppException(MemberExceptions.UNEXISTS_MEMBER);
                });

        }

}
