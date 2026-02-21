package com.example.stomp.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler
        implements AuthenticationEntryPoint, AuthenticationFailureHandler, AccessDeniedHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override 
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) {
        eventPublisher
                .publishEvent(new AuthErrorEvent(request, response, authException, AuthErrorType.UNAUTHENTICATED));
    }

    @Override 
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        eventPublisher.publishEvent(new AuthErrorEvent(request, response, exception, AuthErrorType.LOGIN_FAILURE));
    }

    @Override 
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) {
        eventPublisher.publishEvent(
                new AuthErrorEvent(request, response, accessDeniedException, AuthErrorType.ACCESS_DENIED));
    }

    // --- 2. 필터 예외 처리용 브릿지 메서드 (외부 필터에서 호출용) ---

    public void handleFilterException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        eventPublisher.publishEvent(new AuthErrorEvent(request, response, e, AuthErrorType.FILTER_ERROR));
    }

    // --- 3. [INNER] 이벤트 클래스 (전령) ---
    // 외부에서 굳이 이 클래스를 알 필요가 없으므로 내부 private static으로 선언
    @Getter
    @RequiredArgsConstructor
    private static class AuthErrorEvent {
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final Exception exception;
        private final AuthErrorType type;
    }

    // --- 4. [INNER] 에러 타입 정의 (명세서) ---
    @Getter
    @RequiredArgsConstructor
    public enum AuthErrorType {
        UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "AUTH_001", "인증이 필요한 서비스입니다."),
        LOGIN_FAILURE(HttpStatus.UNAUTHORIZED, "AUTH_002", "소셜 로그인에 실패했습니다."),
        ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_003", "해당 리소스에 대한 권한이 없습니다."),
        FILTER_ERROR(HttpStatus.UNAUTHORIZED, "AUTH_004", "유효하지 않은 토큰입니다.");

        private final HttpStatus httpStatus;
        private final String errorCode;
        private final String defaultMessage;
    }

    // --- 5. [INNER] 통합 리스너 (실제 짬 처리반) ---
    @Component
    class SecurityErrorEventListener {

        @EventListener
        public void onAuthError(AuthErrorEvent event) throws IOException {
            HttpServletResponse response = event.getResponse();
            AuthErrorType type = event.getType();
            Exception ex = event.getException();

            response.setStatus(type.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            // AppException(우리가 만든 추상 예외) 계열이면 해당 메시지 추출
            String message = (ex instanceof AppException) ? ex.getMessage() : type.getDefaultMessage();

            // objectMapper.writeValue(response.getWriter(),
            // ApiResponse.createErrorResponse(type.getErrorCode(), message));
        }
    }
}