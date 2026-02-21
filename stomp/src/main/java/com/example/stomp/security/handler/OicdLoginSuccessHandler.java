package com.example.stomp.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.stomp.jwt.dto.CreateAccessTokenDto;
import com.example.stomp.jwt.dto.CreateRefreshTokenDto;
import com.example.stomp.jwt.dto.CreateRefreshTokenResponse;
import com.example.stomp.jwt.service.JwtService;
import com.example.stomp.security.util.SecurityUtils;
import com.example.stomp.shared.dto.ApiResponse;
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
        DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();

        CreateAccessTokenDto accessTokenDto = new CreateAccessTokenDto(
                oidcUser.getEmail(),
                SecurityUtils.authoritiesToString(oidcUser.getAuthorities()));

        CreateRefreshTokenDto refreshTokenDto = new CreateRefreshTokenDto(
                oidcUser.getEmail());

        String accessToken = jwtService.createAccessToken(accessTokenDto);
        CreateRefreshTokenResponse refreshTokenResponse = jwtService.createAndSaveRefreshToken(refreshTokenDto);

        response.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("refreshToken", refreshTokenResponse.refreshToken())
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(refreshTokenResponse.maxAgeSeconds())
                        .sameSite(SameSite.LAX.toString())
                        .build().toString());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), ApiResponse.createDefaultSuccessResponse(accessToken));
    }

}
