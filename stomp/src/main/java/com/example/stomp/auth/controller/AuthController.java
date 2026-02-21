package com.example.stomp.auth.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.jwt.config.JwtContants;
import com.example.stomp.jwt.dto.CreateAccessTokenDto;
import com.example.stomp.jwt.service.JwtService;
import com.example.stomp.member.domain.Member;
import com.example.stomp.member.repository.MemberRepository;
import com.example.stomp.shared.argresolver.AccessToken;
import com.example.stomp.shared.dto.ApiResponse;
import com.example.stomp.shared.util.CookieUtil;
import com.example.stomp.shared.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final JwtService jwtService;
        private final MemberRepository memberRepository;

        @PostMapping("/reissue")
        public ResponseEntity<ApiResponse.Success<String>> reissue(
                        @AccessToken String accessToken,
                        @CookieValue(name = CookieUtil.REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
                // 재발급은 필터를 안 거치니까 컨트롤러에서 벨리데이션
                jwtService.validateToken(refreshToken);

                jwtService.blackAccessToken(accessToken, JwtContants.BlackReason.REISSUE);

                Member member = memberRepository.findById(jwtService.getMemberId(refreshToken)).orElseGet(
                                () -> {
                                        throw new IllegalArgumentException();
                                });

                List<GrantedAuthority> authorities = SecurityUtil
                                .stringToAuthorities(member.getMemberRole().toString());

                return ApiResponse
                                .createDefaultSuccessResponse(jwtService.createAccessToken(
                                                new CreateAccessTokenDto(member.getId(),
                                                                SecurityUtil.authoritiesToString(authorities))))
                                .toResponseEntity();
        }

        @PostMapping("/logout")
        public ResponseEntity<ApiResponse.Success<Void>> logout(
                        @AccessToken String accessToken,
                        @CookieValue(name = CookieUtil.REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
                jwtService.deleteRefreshToken(refreshToken);
                jwtService.blackAccessToken(accessToken, JwtContants.BlackReason.LOGOUT);

                ResponseCookie cookie = CookieUtil.createRefreshTokenCookie(null, 0);

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(ApiResponse.createEmptySuccessResponse());
        }

}
