package com.example.stomp.app.util;

import java.time.Duration;
import java.util.Optional;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.example.stomp.app.constant.SessionConstant;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request) {
        for (Cookie cookie : Optional.ofNullable(request.getCookies()).orElse(new Cookie[] {})) {
            if (SessionConstant.COOKIE_NAME.equals(cookie.getName())) {
                return Optional.of(cookie);
            }
        }

        return Optional.empty();
    }

    public static void setCookie(String sessionId, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(SessionConstant.COOKIE_NAME, sessionId)
                .secure(true)
                .httpOnly(true)
                .maxAge(Duration.ofDays(1))
                .sameSite(SameSite.LAX.toString())
                .path(SessionConstant.COOKIE_PATH)
                .domain("app.github.dev")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
