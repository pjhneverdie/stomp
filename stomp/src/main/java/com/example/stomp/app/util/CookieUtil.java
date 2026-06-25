package com.example.stomp.app.util;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.example.stomp.app.constant.SessionKeys;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtil {

    private CookieUtil() {
    }

    public static Optional<Cookie> getLoginCookie(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> SessionKeys.COOKIE_NAME.equals(cookie.getName()))
                .findFirst();
    }

    public static void setLoginCookie(String sessionId, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(SessionKeys.COOKIE_NAME, sessionId)
                .secure(true)
                .httpOnly(true)
                .maxAge(Duration.ofDays(1))
                .sameSite(SameSite.LAX.toString())
                .path(SessionKeys.COOKIE_PATH)
                .domain("app.github.dev")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
