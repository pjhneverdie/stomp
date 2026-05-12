package com.example.stomp.chat.ws.stomp.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.infra.websocket.WsMemberPrincipal;
import com.example.stomp.app.util.StompHeaderUtil;
import com.example.stomp.chat.service.ChatCleanUpService;
import com.example.stomp.chat.service.ChatRoomService;
import com.example.stomp.security.repository.RedisHttpSessionContextRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSubscriptionInterceptor implements ChannelInterceptor {

    private final ChatRoomService chatRoomService;

    private final ChatCleanUpService chatCleanUpService;

    private final RedisHttpSessionContextRepository redisHttpSessionContextRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        WsMemberPrincipal wsMpc = StompHeaderUtil.getPrincipal(accessor);

        // This is just a heartbeat.
        if (accessor.getCommand() == null) {
            return message;
        }

        switch (accessor.getCommand()) {
            case RECEIPT: {
                chatRoomService.join(wsMpc.getRoomUUID(), wsMpc.getId(), wsMpc.getNickname());

                redisHttpSessionContextRepository.setWsSessionId(accessor.getSessionId());

            }
                break;

            default:
                break;
        }

        return message;
    }

}