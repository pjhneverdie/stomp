package com.example.stomp.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.stomp.shared.dto.ApiResponse;
import com.example.stomp.shared.dto.exception.AppException;
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
        dealLikeAppException(response, authException, HttpStatus.UNAUTHORIZED);
    }

    // handle Exception caused by AuthenticationFailureHandler
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        dealLikeAppException(response, exception, HttpStatus.UNAUTHORIZED);
    }

    // handle Exception caused by AccessDeniedHandler
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        dealLikeAppException(response, accessDeniedException, HttpStatus.FORBIDDEN);
    }

    // handle Exception caused by Filter
    public void handleFilterException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws IOException {
        switch (e) {
            case AppException appException:
                sendFailureResponse(response, appException);
                break;

            // unexpected exception
            default:
                dealLikeAppException(response, e, HttpStatus.INTERNAL_SERVER_ERROR);
                break;
        }
    }

    private void dealLikeAppException(HttpServletResponse response, Exception e, HttpStatus status)
            throws IOException {

        // convert each Exception to AppException
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

}