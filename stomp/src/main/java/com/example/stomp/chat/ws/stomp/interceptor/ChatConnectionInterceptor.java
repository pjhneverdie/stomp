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
import com.example.stomp.chat.service.ChatRoomService;
import com.example.stomp.chat.service.ChatService;

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

              
                // CONNECT 단계 때는
                // 1. 헤더에 roomId, nickname 받아서
                // 2. 자리 있는지, 없으면 내가 자리 차지 했는지 확인하고 통과하면
                // 3. 닉네임 받아서 wspc에 넣고
                // 4. roomId wspc에 넣고

                // 그다음 구독을 성공하면 ChatSubscriptionInterceptor
                // String roomUUID = (String) accessor.getHeader("roomUUID");

                // // 1. 방에 들어갈 수 있는지(자리 남았거나 자리가 찼으면 그 중 내가 있어야함)

                // Optional.ofNullable(wsPrincipal.getRoomId()).ifPresent((roomId) -> {

                // // 1. 방에 들어갈 수 있는지(자리가 있는지)
                // // 2. 자리가 다 찼다면 그 중 내가 있는지
                // // 3.

                // // ChatRoom chatRoom = chatRoomService.orElseThrow(roomId);

                // // /**
                //     //  * @formatter:off
                //     //  * 
                //     //  * 1. Check if a user owns pass code.
                //     //  * 2. Check if a user owns only one connection.
                //     //  * 
                //     //  * @formatter:on
                // // */
                // // chatRoom.validatePassCode(wsPrincipal.getMemberCode());
                // // chatRoom.validateConnection(wsPrincipal.getMemberId());
                // });
            }
                break;

            default:
                break;
        }

        return message;
    }

}

// 아니 잘봐.
// 이제는 이렇게 해야해.
// 일단 채팅방 히스토리를 볼 때는 END로 검색해서 가져오고,
// 한 번에 두 개 이상 채팅방 참여가 불가능하니까, 첨에 WSPrincipal 만들 때 현재 진행 중인 roomId를 넣는거야.
//