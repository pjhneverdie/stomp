package com.example.stomp.chat.ws.stomp.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.example.stomp.app.util.StompHeaderUtil;
import com.example.stomp.chat.service.ChatRoomService;
import com.example.stomp.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSubscriptionInterceptor implements ChannelInterceptor {

    private final ChatRoomService chatRoomService;

    private final MemberService memberService;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        // This is just a heartbeat.
        if (accessor.getCommand() == null) {
            return message;
        }

        switch (accessor.getCommand()) {
            case SEND: {
                // 메시지 roomUuid 없으면 거절 등 validation.
            }
                break;

            case RECEIPT: {
                // String nickname = (String) accessor.getHeader("nickname");
                // String roomUUID = (String) accessor.getHeader("roomUUID");
            }
                break;

            default:
                break;
        }

        return message;
    }

}