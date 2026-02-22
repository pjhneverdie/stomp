package com.example.stomp.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.auth.serivce.AuthService;
import com.example.stomp.shared.argresolver.AccessTokenHeader;
import com.example.stomp.shared.dto.ApiResponse;
import com.example.stomp.shared.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @PostMapping("/reissue")
        public ResponseEntity<ApiResponse.Success<String>> reissue(
                        @AccessTokenHeader String preAccessToken,
                        HttpServletRequest request) {
                long memberId = Long.parseLong((String) request.getAttribute("memberId"));

                return ApiResponse
                                .createDefaultSuccessResponse(authService.createAccessToken(preAccessToken, memberId))
                                .toResponseEntity();
        }

        @PostMapping("/logout")
        public ResponseEntity<ApiResponse.Success<Void>> logout(
                        @AccessTokenHeader String accessToken,
                        @CookieValue(name = CookieUtil.REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
                authService.nullifyTokens(accessToken, refreshToken);

                ResponseCookie cookie = CookieUtil.createRefreshTokenCookie(null, 0);

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(ApiResponse.createEmptySuccessResponse());
        }

}
