package com.example.stomp.chat.ws.stomp.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.example.stomp.app.infra.websocket.WsMemberPrincipal;
import com.example.stomp.app.util.StompHeaderUtil;
import com.example.stomp.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSubscriptionInterceptor implements ChannelInterceptor {

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
            case RECEIPT: {

                Boolean isReconnect = wsPrincipal.getRoomId() != null;
                String nickname = null;

                if (!isReconnect) {
                    nickname = (String) accessor.getHeader("nickname");
                }

                chatRoomService.participate(accessor.getReceiptId(),
                        wsPrincipal.getMemberId(), nickname, isReconnect);

            }
                break;

            default:
                break;
        }

        return message;
    }

}