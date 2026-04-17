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
            /**
             * @formatter:off
             * 
             * On CONNECT step, we have to validate if the user can join or not.
             * 
             * 1. validate if the user has valid passcode of the room.
             * 2. validate if user tries to join the room with the same account but multiple session.
             * 
             * @formatter:on
             */
            case CONNECT: {
                ChatRoom chatRoom = chatRoomService.validateIfChatRoomExists(ROOM_ID_HEADER_KEY);

                // 1.
                chatRoom.validatePassCode(wsPrincipal.getMemberCode());

                // 2.
                JoinType joinType = chatRoom.validateSession(wsPrincipal.getRoomId());

                if (joinType == JoinType.RECONNECTION) {

                }

                // 3. Save the id of the room user is going to join into WSsession.
                wsPrincipal.setRoomId(ROOM_ID_HEADER_KEY);
            }

            case SUBSCRIBE:
                // if
                // redis.opsForValue().set("chatroom:" + roomId + ":presence:" + memberId,
                // sessionId);
                // 이렇게 저장할건데 만약 sessionId 없으면 접속 시키고
                // 있으면 다중창 띄워서 접속하는 거니까 최대 세션 수 제한 에러 띄우면 돼

                // SimpleWsPrincipal에 roomId 추가
                // String roomIdㅇ = accessor.getDestination();

                // log.info("구독하겠습니다. " + roomId);

                // WsPrincipal principal = (WsPrincipal) accessor.getUser();
                // principal.setRoomId(roomId);

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