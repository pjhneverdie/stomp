package com.example.stomp.app.infra.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.stomp.app.util.StompHeaderUtil;
import com.example.stomp.security.repository.RedisHttpSessionContextRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketSessionManager {

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

        redisHttpSessionContextRepository.deleteWsSessionId(principal.getWsSessionId());
    }

}
