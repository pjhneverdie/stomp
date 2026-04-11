package com.example.stomp.chat.ws.stomp.interceptor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

import com.example.stomp.chat.dto.SimpleWsPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        log.info(accessor.getCommand().toString());

        switch (accessor.getCommand()) {
            case CONNECT:
                log.info("2.STOMP 프로토콜로 할게요~");
                SimpleWsPrincipal user = (SimpleWsPrincipal) accessor.getUser();

                log.info(user.getSimpleMemberDetails().authorities().toString());

                break;
            case SUBSCRIBE:

                break;
            case SEND:

                break;

            default:
                break;
        }

        return message;
    }

}