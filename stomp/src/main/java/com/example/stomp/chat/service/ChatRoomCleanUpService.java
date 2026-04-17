package com.example.stomp.chat.service;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.example.stomp.app.infra.websocket.WsPrincipal;
import com.example.stomp.app.util.StompHeaderUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatRoomCleanUpService {

    @EventListener
    public void handleWebSocketConnectedListener(SessionSubscribeEvent event) {
        // 내일 구독 시에 목적지 얻을 수 있는지 확인하기.
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        WsPrincipal wsPrincipal = StompHeaderUtil.getPrincipal(accessor);
        System.out.println(accessor.getDestination());

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        log.info(accessor.getUser().getName());
        log.info("연결 끊겼다.");
        log.info("연결 끊겼다.");
    }

}
