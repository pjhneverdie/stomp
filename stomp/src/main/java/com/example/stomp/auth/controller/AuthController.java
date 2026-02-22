package com.example.stomp.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.auth.dto.RtrTokenDto;
import com.example.stomp.auth.serivce.AuthService;
import com.example.stomp.jwt.config.JwtContants.BlackReason;
import com.example.stomp.security.filter.LoginFilter;
import com.example.stomp.shared.argresolver.AccessTokenHeader;
import com.example.stomp.shared.dto.ApiResponse;
import com.example.stomp.shared.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @PostMapping("/reissue")
        public ResponseEntity<ApiResponse.Success<String>> reissue(
                        @AccessTokenHeader String preAccessToken,
                        @CookieValue(name = CookieUtil.REFRESH_TOKEN_COOKIE_NAME) String preRefreshToken,
                        HttpServletRequest request, HttpServletResponse response) {
                long memberId = Long.parseLong((String) request.getAttribute(LoginFilter.MEMBER_ID_KEY));

                RtrTokenDto tokenResponse = authService.rtr(preAccessToken,
                                preRefreshToken, memberId);

                response.addHeader(HttpHeaders.SET_COOKIE, CookieUtil.createRefreshTokenCookie(
                                tokenResponse.refreshTokenDto().refreshToken(),
                                tokenResponse.refreshTokenDto().maxAgeSec()).toString());

                return ApiResponse
                                .createDefaultSuccessResponse(tokenResponse.accessToken())
                                .toResponseEntity();
        }

        @PostMapping("/logout")
        public ResponseEntity<ApiResponse.Success<Void>> logout(
                        @AccessTokenHeader String accessToken,
                        @CookieValue(name = CookieUtil.REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
                authService.nullifyTokens(accessToken, refreshToken, BlackReason.LOGOUT);

                ResponseCookie cookie = CookieUtil.createRefreshTokenCookie(null, 0);

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(ApiResponse.createEmptySuccessResponse());
        }

}
