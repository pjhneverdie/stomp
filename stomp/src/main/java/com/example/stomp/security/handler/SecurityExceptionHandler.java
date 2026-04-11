package com.example.stomp.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.stomp.app.dto.ApiResponse;
import com.example.stomp.app.dto.exception.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler
        implements AuthenticationEntryPoint, AuthenticationFailureHandler, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    // handle Exception caused by AuthenticationEntryPoint
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        logBriefError("AuthenticationEntryPoint", authException);
        convertToAppException(response, authException, HttpStatus.UNAUTHORIZED);



        
    }

    // handle Exception caused by AuthenticationFailureHandler
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        logBriefError("AuthenticationFailureHandler", exception);
        convertToAppException(response, exception, HttpStatus.UNAUTHORIZED);
    }

    // handle Exception caused by AccessDeniedHandler
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        logBriefError("AccessDeniedHandler", accessDeniedException);
        convertToAppException(response, accessDeniedException, HttpStatus.FORBIDDEN);
    }

    private void convertToAppException(HttpServletResponse response, Exception e, HttpStatus status)
            throws IOException {
        AppException appException = new AppException(e.getMessage()) {
            @Override
            public HttpStatus getHttpStatus() {
                return status;
            }
        };

        sendFailureResponse(response, appException);
    }

    private void sendFailureResponse(HttpServletResponse response, AppException e) throws IOException {
        response.setStatus(e.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter().write(
                objectMapper.writeValueAsString(ApiResponse.createDefaultFailureResponse(e)));
    }

    private void logBriefError(String handlerName, Exception e) {
        Throwable rootCause = e;

        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }

        StackTraceElement firstLine = rootCause.getStackTrace()[0];
        
        System.out.printf("[%s] 근본 원인: %s - %s (발생위치: %s:%d)%n",
                handlerName,
                rootCause.getClass().getSimpleName(),
                rootCause.getMessage(),
                firstLine.getFileName(),
                firstLine.getLineNumber());
    }

}