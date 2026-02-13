package com.example.stomp.chat.websocket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.example.stomp.chat.repository.ChatPresenceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatInterceptor implements ChannelInterceptor {
    private final ChatPresenceRepository chatPresenceRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        switch (accessor.getCommand()) {
            case CONNECT:

                // 여기서는 JWT 인증

                break;
            case SUBSCRIBE:

                // 여기는 이미 CONNECT를 거쳐야 올 수 있음.
                // 즉 유저 정보를 이미 StompHeaderAccessor가 다 가지고 있음.
                String roomId = accessor.getDestination();
                String memberId = accessor.getUser().getName();
                String currentSessionId = accessor.getSessionId();

                chatPresenceRepository.getSessionId(memberId).ifPresent((old) -> {
                    // 예외를 발생 시킴. StompSubProtocolErrorHandler가 잡아서 에러 프레임 전송, 클라이언트에서 연결 거절.
                    // 이걸 쓰면 진짜 에러 프레임 나가고 
                    // 컨트롤러 내의 비즈니스 예외는 @MessageExceptionHandler 를 써서 메시지로 바꿔 보내야 함. !!
                });

                break;
            case SEND:

                break;

            default:
                break;
        }

        return message;
    }
}