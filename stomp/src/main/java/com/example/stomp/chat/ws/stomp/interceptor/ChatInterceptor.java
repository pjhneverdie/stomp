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

// 일단 지금 단계에서 해야하는 게 설계야 설계
// 인터넷이 끊기든, 창을 닫아 버리든, 새로고침을 하든 이걸 서버에서 다 감지를 해야해  

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // this is just a heartbeat
        if (accessor.getCommand() == null) {
            return message;
        }

        switch (accessor.getCommand()) {
            case SUBSCRIBE:
                // redis.opsForValue().set("chatroom:" + roomId + ":presence:" + memberId,
                // sessionId);
                // 이렇게 저장할건데 만약 sessionId 없으면 접속 시키고
                // 있으면 다중창 띄워서 접속하는 거니까 최대 세션 수 제한 에러 띄우면 돼

                // SimpleWsPrincipal에 roomId 추가
                String roomId = accessor.getDestination();

                log.info("구독하겠습니다. " + roomId);

                SimpleWsPrincipal principal = (SimpleWsPrincipal) accessor.getUser();
                principal.setRoomId(roomId);

                break;
            case SEND:

                log.info("메시지좀 보낼게여");
                break;

            default:
                break;
        }

        return message;
    }

}