package com.example.stomp.app.util;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.example.stomp.app.constant.SessionConstant;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private Cookie getCookie(HttpServletRequest request) {
        return Stream.ofNullable(request.getCookies())
                .flatMap(Arrays::stream)
                .filter(c -> SessionConstant.COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .orElse(null);
    }

    private void setCookie(String sessionId, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(SessionConstant.COOKIE_NAME, sessionId)
                .secure(true)
                .httpOnly(true)
                .maxAge(Duration.ofDays(1))
                .sameSite(SameSite.LAX.toString())
                .path(SessionConstant.COOKIE_PATH)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
