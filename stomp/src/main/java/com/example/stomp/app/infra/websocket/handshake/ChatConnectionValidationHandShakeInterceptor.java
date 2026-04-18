package com.example.stomp.app.infra.websocket.handshake;

import java.util.Map;
import java.util.Optional;

import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;

import com.example.stomp.app.util.SecurityUtil;
import com.example.stomp.chat.service.ChatRoomService;
import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatConnectionValidationHandShakeInterceptor implements HandshakeInterceptor {

    private final ChatRoomService chatRoomService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        RedisHttpSessionMemberPrincipal memberDetails = SecurityUtil.getPrincipal();

        /**
         * @formatter:off
         * 
         * If id of the room exists in user's session,
         * we need to determine whether this access is reconnection or extra connection(we allowed only one connection).
         * 
         * @formatter:on
         */
        Optional.ofNullable(memberDetails.getRoomId()).ifPresent((roomId) -> {
            chatRoomService.orElseThrow(roomId).validateConnection(String.valueOf(memberDetails.getId()));
        });

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {

    }

}
