package com.example.stomp.shared.util;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;

public abstract class CookieUtil {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    public static ResponseCookie createRefreshTokenCookie(String refreshToken, long maxAgeSec) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAgeSec)
                .sameSite(SameSite.LAX.toString())
                .build();
    }

}
