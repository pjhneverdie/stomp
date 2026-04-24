package com.example.stomp.chat.ws.stomp.interceptor;

import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.example.stomp.app.infra.websocket.WsMemberPrincipal;
import com.example.stomp.app.util.StompHeaderUtil;
import com.example.stomp.chat.document.ChatRoom;
import com.example.stomp.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatConnectionInterceptor implements ChannelInterceptor {

    private final ChatService chatRoomService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        WsMemberPrincipal wsPrincipal = StompHeaderUtil.getPrincipal(accessor);

        // This is just a heartbeat.
        if (accessor.getCommand() == null) {
            return message;
        }

        switch (accessor.getCommand()) {
            case CONNECT: {
                Optional.ofNullable(wsPrincipal.getRoomId()).ifPresent((roomId) -> {
                    ChatRoom chatRoom = chatRoomService.orElseThrow(roomId);

                    /**
                     * @formatter:off
                     * 
                     * 1. Check if a user owns pass code.
                     * 2. Check if a user owns only one connection.
                     * 
                     * @formatter:on
                     */
                    chatRoom.validatePassCode(wsPrincipal.getMemberCode());
                    chatRoom.validateConnection(wsPrincipal.getMemberId());
                });
            }
                break;

            default:
                break;
        }

        return message;
    }

}