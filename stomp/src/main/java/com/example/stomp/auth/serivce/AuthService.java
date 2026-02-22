package com.example.stomp.auth.serivce;

import org.springframework.stereotype.Service;

import com.example.stomp.jwt.config.JwtContants;
import com.example.stomp.jwt.dto.CreateAccessTokenDto;
import com.example.stomp.jwt.service.JwtService;
import com.example.stomp.member.domain.Member;
import com.example.stomp.member.repository.MemberRepository;
import com.example.stomp.shared.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    public String createAccessToken(String preAccessToken, long memberId) {
        jwtService.blackAccessToken(preAccessToken, JwtContants.BlackReason.REISSUE);

        Member member = memberRepository.findById(memberId).orElseGet(
                () -> {
                    throw new IllegalArgumentException();
                });

        return jwtService.createAccessToken(
                new CreateAccessTokenDto(member.getId(),
                        SecurityUtil.authoritiesToString(
                                member.getAuthorities())));
    }

    public void nullifyTokens(String accessToken, String refreshToken) {
        jwtService.deleteRefreshToken(refreshToken);
        jwtService.blackAccessToken(accessToken, JwtContants.BlackReason.LOGOUT);
    }

}