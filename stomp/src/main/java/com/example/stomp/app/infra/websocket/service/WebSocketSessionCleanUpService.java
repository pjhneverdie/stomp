package com.example.stomp.app.infra.websocket.service;

import java.io.IOException;
import java.util.Collections;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.stomp.app.infra.websocket.WsMemberPrincipal;
import com.example.stomp.app.util.StompHeaderUtil;
import com.example.stomp.chat.dto.ChatMessageSendReq;
import com.example.stomp.security.repository.RedisHttpSessionContextRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketSessionCleanUpService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final WebSocketSessionStore inAppSessionStore;
    private final RedisHttpSessionContextRepository redisHttpSessionContextRepository;

    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);

        WsMemberPrincipal principal = StompHeaderUtil.getPrincipal(accessor);

        principal.setWsSessionId(accessor.getSessionId());

        redisHttpSessionContextRepository.setWsSessionId(
                principal.getHttpSessionId(),
                principal.getWsSessionId());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        WsMemberPrincipal principal = StompHeaderUtil
                .getPrincipal(MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class));

        redisHttpSessionContextRepository.deleteWsSessionId(principal.getHttpSessionId());
    }

    public void forceTerminate(String memberId, String wsSessionId) {
        kafkaTemplate.send("", "");
    }

    @KafkaListener(topics = "", groupId = "", concurrency = "")
    public void sessionSwitchListener(String memberId, String wsSessionId) {
        WebSocketSession session = inAppSessionStore.get(wsSessionId);

        if (session != null && session.isOpen()) {
            try {
                session.close(CloseStatus.NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
