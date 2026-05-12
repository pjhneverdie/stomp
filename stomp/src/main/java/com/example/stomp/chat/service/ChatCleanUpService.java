package com.example.stomp.chat.service;

import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.infra.websocket.WsMemberPrincipal;
import com.example.stomp.app.util.StompHeaderUtil;
import com.example.stomp.chat.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatCleanUpService {

    private final StringRedisTemplate redis;
    private final ChatRoomRepository chatRoomRepository;

    public void persist() {
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        log.info(accessor.getUser().getName());
        log.info("연결 끊겼다.");
        log.info("연결 끊겼다.");

        WsMemberPrincipal pc = StompHeaderUtil.getPrincipal(accessor);

        redis.opsForHash().delete(SessionConstant.SESSION_HKEY_PREFIX + pc.getHttpSessionId(),
                SessionConstant.SESSION_WS_SESSION_ID_FKEY);


    }

}
