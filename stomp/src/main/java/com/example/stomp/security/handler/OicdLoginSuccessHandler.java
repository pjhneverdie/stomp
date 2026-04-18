package com.example.stomp.security.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OicdLoginSuccessHandler implements AuthenticationSuccessHandler {

        private final String frontendOrigin;

        public OicdLoginSuccessHandler(
                        @Value("${frontend-origin}") String frontendOrigin) {
                this.frontendOrigin = frontendOrigin;
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                response.sendRedirect(frontendOrigin);
        }

}
