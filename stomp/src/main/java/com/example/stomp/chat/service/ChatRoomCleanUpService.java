package com.example.stomp.chat.service;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.example.stomp.app.infra.websocket.WsMemberPrincipal;
import com.example.stomp.app.util.StompHeaderUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomCleanUpService {

    private final ChatRoomService chatRoomService;

    @EventListener
    public void handleWebSocketConnectedListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        WsMemberPrincipal wsPrincipal = StompHeaderUtil.getPrincipal(accessor);

        // boolean isReconnect = wsPrincipal.getRoomId() != null;

        // chatRoomService.participate(accessor.getDestination(), wsPrincipal.getMemberId(), isReconnect);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        log.info(accessor.getUser().getName());
        log.info("연결 끊겼다.");
        log.info("연결 끊겼다.");
    }

}
