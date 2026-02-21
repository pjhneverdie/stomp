package com.example.stomp.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.stomp.jwt.dto.CreateAccessTokenDto;
import com.example.stomp.jwt.dto.CreateRefreshTokenDto;
import com.example.stomp.jwt.dto.CreateRefreshTokenResponse;
import com.example.stomp.jwt.service.JwtService;
import com.example.stomp.member.dto.OidcMemberDetails;
import com.example.stomp.shared.dto.ApiResponse;
import com.example.stomp.shared.util.CookieUtil;
import com.example.stomp.shared.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OicdLoginSuccessHandler implements AuthenticationSuccessHandler {

        private final JwtService jwtService;
        private final ObjectMapper objectMapper;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                OidcMemberDetails memberDetails = (OidcMemberDetails) authentication.getPrincipal();

                String accessToken = jwtService.createAccessToken(new CreateAccessTokenDto(
                                memberDetails.getMemberId(),
                                SecurityUtil.authoritiesToString(memberDetails.getAuthorities())));

                CreateRefreshTokenResponse refreshTokenResponse = jwtService
                                .createAndSaveRefreshToken(new CreateRefreshTokenDto(
                                                memberDetails.getMemberId()));

                response.addHeader(HttpHeaders.SET_COOKIE,
                                CookieUtil.createRefreshTokenCookie(
                                                refreshTokenResponse.refreshToken(),
                                                refreshTokenResponse.maxAgeSec())
                                                .toString());

                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());

                objectMapper.writeValue(response.getWriter(), ApiResponse.createDefaultSuccessResponse(accessToken));
        }

}
