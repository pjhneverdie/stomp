package com.example.stomp.security.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RedisHttpSessionLogoutSuccessHandler implements LogoutSuccessHandler {

    private final String FRONTEND_ORIGIN;

    public RedisHttpSessionLogoutSuccessHandler(
            @Value("${frontend-origin}") String frontendOrigin) {
        this.FRONTEND_ORIGIN = frontendOrigin;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        response.sendRedirect(FRONTEND_ORIGIN);
    }

}
