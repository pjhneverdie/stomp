package com.example.stomp.chat.service;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.repository.ChatRoomRepository;
import com.example.stomp.security.dto.SimpleAuthenticationToken;
import com.example.stomp.security.dto.SimpleAuthenticationToken.SimpleMemberDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final RedisTemplate<String, Object> redis;
    private final ChatRoomRepository chatRoomRepository;
    private final ObjectMapper objectMapper;

    private final static String CHATROOM_KEY_PREFIX = "chatroom:";
    private final static String CHATROOM_NAME_KEY = "name";
    private final static String CHATROOM_PASS_CODES_KEY = "passCodes";

    public boolean isJoinable(String roomId, String code) {
        return chatRoomRepository.findById(roomId)
                .map(chatRoom -> chatRoom.isJoinable(code))
                .orElse(false);
    }

    public String create(String name, List<String> passCodes) {
        return chatRoomRepository.save(ChatRoom.create(UUID.randomUUID().toString(), name, passCodes)).getId();
    }

    public void comeIn(String roomId, String memberId, String sessionId, String code) throws Exception {
        Map<Object, Object> chatroom = redis.opsForHash().entries(CHATROOM_KEY_PREFIX + roomId);

        if (chatroom != null) {
            String codes = (String) chatroom.get(CHATROOM_PASS_CODES_KEY);
            List<String> codeList = objectMapper.readValue(codes, new TypeReference<List<String>>() {
            });

            if (codeList.contains(code)) {

                String sessionId2 = (String) redis.opsForValue().get("chatroom:" + roomId + ":presence:" + memberId);

                if (sessionId2 != null) {
                    // 다중 창 접속임 나중에 처리

                } else {
                    redis.opsForValue().set("chatroom:" + roomId + ":presence:" + memberId, sessionId);

                    // save that he has a chat for preventing him from reckless creation
                    // once the chat ends, it should be replaced as null
                    redis.opsForHash().put(SessionConstant.SESSION_KEY_PREFIX + sessionId,
                            SessionConstant.SESSION_ROOM_ID_KEY,
                            roomId);
                }
            }
        } else {
        }

    }

}

/**
 * 1. 회원가입 시 고유 코드 발급
 * 2. 채팅방 생성 시 상대방 코드랑 자기 코드를 등록해서 등록한 사람만 올 수 있게 함
 */

/**
 * chatroom:{roomId}:presence:{memberId} / sessionId
 * chatroom:{roomId}:presence:{memberId} / code
 * 
 * 이렇게 저장.
 * 
 * 연결 끊길 시, 해당 member OpsForvalue에 TTL5분 검
 * 5분 뒤에 삭제되면 스프링에서 이벤트 받아서 해당 채팅방 채팅내역까서 5분동안 아무 말 없었으면 채팅방 삭제, 있으면 그냥 유지
 * 이러면 유저가 재접속하면 어차피
 */