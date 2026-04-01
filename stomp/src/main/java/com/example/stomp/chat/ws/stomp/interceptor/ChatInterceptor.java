package com.example.stomp.chat.ws.stomp.interceptor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.example.stomp.chat.dto.SimpleWsPrincipal;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatInterceptor implements ChannelInterceptor {
    private final RedisTemplate<String, Object> redis;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        switch (accessor.getCommand()) {
            case CONNECT:
                SimpleWsPrincipal user = (SimpleWsPrincipal) accessor.getUser();
                
                break;
            case SUBSCRIBE:

                String roomId = accessor.getDestination();
                String memberId = accessor.getUser().getName();

                String currentSessionId = accessor.getSessionId();

                break;
            case SEND:

                break;

            default:
                break;
        }

        return message;
    }
}