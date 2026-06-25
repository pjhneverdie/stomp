package com.example.stomp.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.stomp.app.dto.ApiResponse;
import com.example.stomp.app.dto.AppException;
import com.example.stomp.app.dto.ExceptionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityExceptionHandler
        implements AuthenticationEntryPoint, AuthenticationFailureHandler, AccessDeniedHandler {

    private final String LOGIN_FAILED_REDIRECT_URL;
    private final ObjectMapper objectMapper;

    public SecurityExceptionHandler(
            @Value("${login-failed-redirect-url}") String loginFailedRedirectUrl,
            @Autowired ObjectMapper objectMapper) {
        this.LOGIN_FAILED_REDIRECT_URL = loginFailedRedirectUrl;
        this.objectMapper = objectMapper;
    }

    // From AuthenticationFailureHandler.
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        response.sendRedirect(
                LOGIN_FAILED_REDIRECT_URL + String.format("?message=%s", exception.getMessage()));
    }

    // From AuthenticationEntryPoint.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        sendFailureResponse(
                response,
                convertToAppException(response, authException, HttpStatus.UNAUTHORIZED));
    }

    // From AccessDeniedHandler.
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        sendFailureResponse(
                response,
                convertToAppException(response, accessDeniedException, HttpStatus.FORBIDDEN));
    }

    private AppException convertToAppException(HttpServletResponse response, Exception exception, HttpStatus status) {
        return new AppException(new ExceptionInfo() {
            @Override
            public HttpStatus getHttpStatus() {
                return status;
            }

            @Override
            public String getMessage() {
                return exception.getMessage();
            }
        });
    }

    private void sendFailureResponse(HttpServletResponse response, AppException e) throws IOException {
        response.setStatus(e.getExceptionInfo().getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter().write(
                objectMapper.writeValueAsString(ApiResponse.createDefaultFailureResponse(e)));
    }

}