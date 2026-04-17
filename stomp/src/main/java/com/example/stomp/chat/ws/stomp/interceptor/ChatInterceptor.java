package com.example.stomp.chat.ws.stomp.interceptor;

import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.app.infra.websocket.WsPrincipal;
import com.example.stomp.app.util.StompHeaderUtil;
import com.example.stomp.chat.document.ChatRoom;
import com.example.stomp.chat.dto.JoinType;
import com.example.stomp.chat.dto.exception.ChatExceptions;
import com.example.stomp.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 구독 할 때 완벽하게 세션을 복제해야함.
// 1. 일단 채팅방 먼저 생성하고, 유저 redis 세션Map은 손대지마. 구독까지 다 하고 참여중인 채팅방 업데이트 쳐야한다.
// 2. 핸드셰이크할 때 참여중인 채팅방 업데이트하기 이전 세션을 웹소켓 세션에 복제를 쳐놔. 
// 3. 그리고 CONNECT 헤더에 roomId를 보내고, CONNECT 단계에서 roomId로 채팅방 조회해서 code깐다음 웹소켓 세션에 핸드셰이크할 때 세션 복제 쳐놨으니까 code까지는 있을 거야. 
// 이거로 이새기가 연결쳐도 되는 애인지 확인해.
// 4. 그리고 구독 단계에서 웹소켓 세션에 roomId를 넣어서 웹소켓 세션을 완성 시켜.
// 5. 문제없이 SessionSubscribeEvent 뜨면 그때서야 redis 세션 Map에 진짜로 참여중인 채팅방 업데이트쳐.

// disconnected 이벤트 뜨면

// 1. NetworkStatus disconnected로 바꿔, 시간이랑 같이.

// 2. roomId랑 memberId 조합해서 KEY 만들고 10분 TTL STRING 하나 조져놔.

// 3. 재접속하면 TTL 없애고 지워.

// 4. 10분 지나서 삭제되면 이벤트로 KEY받아서 상대도 DISCONNECT인지 확인해.

// 5. 만약 상대도 DISCONNECT 된지 10분 지나면 채팅이랑 이런 거 다 삭제하고 http 세션도 수정해.

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatInterceptor implements ChannelInterceptor {
    private final ChatRoomService chatRoomService;

    private final static String ROOM_ID_HEADER_KEY = "roomId";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        WsPrincipal wsPrincipal = StompHeaderUtil.getPrincipal(accessor);

        // This is just a heartbeat.
        if (accessor.getCommand() == null) {
            return message;
        }

        switch (accessor.getCommand()) {
            case CONNECT: {
                // 1. See If chatroom exists.
                ChatRoom chatRoom = chatRoomService.orElseThrow(ROOM_ID_HEADER_KEY);

                // 2. If it is, validate if they have a right to join.
                chatRoom.validatePassCode(wsPrincipal.getMemberCode());
            }

            case SUBSCRIBE:
                

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