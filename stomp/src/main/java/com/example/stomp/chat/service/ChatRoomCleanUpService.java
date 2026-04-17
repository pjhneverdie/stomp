package com.example.stomp.chat.service;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.stomp.app.infra.websocket.WsPrincipal;
import com.example.stomp.app.util.StompHeaderUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatRoomCleanUpService {

    @EventListener
    public void handleWebSocketDisconnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        WsPrincipal wsPrincipal = StompHeaderUtil.getPrincipal(accessor);

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        log.info(accessor.getUser().getName());
        log.info("연결 끊겼다.");
        log.info("연결 끊겼다.");
    }

}
