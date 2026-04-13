package com.example.stomp.chat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.repository.ChatRoomRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final RedisTemplate<String, Object> redis;
    private final ChatRoomRepository chatRoomRepository;
    private final ObjectMapper objectMapper;

    private final static String CHATROOM_PRESENCE_PREFIX = "chatroom:presence:";

    public String create(long memberId, String name) {
        String roomId = UUID.randomUUID().toString();
        // 혹시 이미 채팅에 참여중인 건 아닌지 확인
        // 이걸 확인하려면
        // 에초에 채팅방에 참여하면 http세션에다가 박아놔야함 참여중인 방을.

        // 채팅방 생성
        Map<String, String> chatRoomInfo = new HashMap<String, String>();

        chatRoomInfo.put("max", "2");
        chatRoomInfo.put("name", name);

        redis.opsForHash().putAll("chatroom:" + roomId, chatRoomInfo);

        redis.opsForHash();

        return roomId;

    }

    public void comeIn(String roomId, String memberId, String sessionId, String code) throws Exception {
        Map<Object, Object> chatroom = redis.opsForHash().entries("chatroom:" + roomId);

        if (chatroom != null) {
            String codes = (String) chatroom.get("codes");
            List<String> codeList = objectMapper.readValue(codes, new TypeReference<List<String>>() {
            });

            if (codeList.contains(code)) {

                String sessionId2 = (String) redis.opsForValue().get("chatroom:" + roomId + ":presence:" + memberId);

                if (sessionId2 != null) {
                    // 다중 창 접속임 나중에 처리

                } else {
                    redis.opsForValue().set("chatroom:" + roomId + ":presence:" + memberId, sessionId);
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