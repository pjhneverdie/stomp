package com.example.stomp.chat.websocket.handler;

import java.nio.charset.StandardCharsets;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    public StompErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

        // 원인 예외 메시지 추출 (래핑된 경우 대비)
        String errorMessage = ex.getMessage();
        if (ex.getCause() != null) {
            errorMessage = ex.getCause().getMessage();
        }

        // 1. STOMP ERROR 커맨드 헤더 생성
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        // 2. 헤더 설정: 'error'라는 키로 요약 정보 전달
        accessor.setNativeHeader("error", "CUSTOM_ERROR_CODE");
        accessor.setLeaveMutable(true);

        // 3. 바디 설정: 상세 메시지를 바이트 배열로 변환 (frame.body로 들어감)
        byte[] payload = errorMessage.getBytes(StandardCharsets.UTF_8);

        return MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
    }
}