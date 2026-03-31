package com.example.stomp.chat.ws.stomp.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        switch (accessor.getCommand()) {
            case CONNECT:
    

                
                break;
            case SUBSCRIBE:

                // 여기는 이미 CONNECT를 거쳐야 올 수 있음.
                // 즉 유저 정보를 이미 StompHeaderAccessor가 다 가지고 있음.
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