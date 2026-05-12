package com.example.stomp.chat.ws.stomp.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.example.stomp.app.infra.websocket.WsMemberPrincipal;
import com.example.stomp.app.util.StompHeaderUtil;
import com.example.stomp.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatConnectionInterceptor implements ChannelInterceptor {

    private final ChatRoomService chatRoomService;

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
                String nickname = (String) accessor.getHeader("nickname");
                String roomUUID = (String) accessor.getHeader("roomUUID");

                /**
                 * @formatter:off
                 * 
                 * All we have to do with connect frame is to validate if he can get in the chat.
                 * - is the chat still existing?
                 * - is there an extra seat to have?
                 * 
                 * @formatter:on
                 */
                Boolean isRecon = chatRoomService.validateIfJoinable(roomUUID, wsPrincipal.getMemberId());

                wsPrincipal.setNickname(nickname);
                wsPrincipal.setRoomUUID(roomUUID);
                wsPrincipal.setIsRecon(isRecon);
            }
                break;

            default:
                break;
        }

        return message;
    }

}