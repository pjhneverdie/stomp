package com.example.stomp.auth.serivce;

import org.springframework.stereotype.Service;

import com.example.stomp.auth.dto.RtrTokenDto;
import com.example.stomp.jwt.config.JwtContants;
import com.example.stomp.jwt.dto.CreateAccessTokenDto;
import com.example.stomp.jwt.dto.CreateRefreshTokenDto;
import com.example.stomp.jwt.dto.RefreshTokenDto;
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

    public RtrTokenDto rtr(String preAccessToken, String preRefreshToken, long memberId) {
        nullifyTokens(preAccessToken, preRefreshToken, JwtContants.BlackReason.REISSUE);

        Member member = memberRepository.findById(memberId).get();

        String accessToken = jwtService.createAccessToken(
                new CreateAccessTokenDto(member.getId(),
                        SecurityUtil.authoritiesToString(
                                member.getAuthorities())));

        RefreshTokenDto refreshTokenResponse = jwtService
                .createAndSaveRefreshToken(new CreateRefreshTokenDto(
                        member.getId()));

        return new RtrTokenDto(accessToken, refreshTokenResponse);
    }

    public void nullifyTokens(String accessToken, String refreshToken, JwtContants.BlackReason reason) {
        jwtService.blackAccessToken(accessToken, reason);
        jwtService.blackRefreshToken(refreshToken, reason);
    }

}