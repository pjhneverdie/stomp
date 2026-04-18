package com.example.stomp.chat.service;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.document.ChatMember;
import com.example.stomp.chat.document.ChatRoom;
import com.example.stomp.chat.document.enum_type.NetworkStatus;
import com.example.stomp.chat.dto.exception.ChatExceptions;
import com.example.stomp.chat.repository.ChatRoomRepository;
import com.example.stomp.security.dto.RedisHttpSessionAuthenticationToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.om.spring.search.stream.EntityStream;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final RedisTemplate<String, Object> redis;
    private final EntityStream entityStream;
    private final ChatRoomRepository chatRoomRepository;

    public static final String CHATROOM_PRESENCE_KEY_PREFIX = "chatRoom:%s:presence:%s";

    public String create(String name, List<String> passCodes) {
        return chatRoomRepository.save(ChatRoom.create(UUID.randomUUID().toString(), name, passCodes)).getId();
    }

    public ChatRoom orElseThrow(String roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow(() -> {
            throw new AppException(ChatExceptions.UNEXISTS_CHAT);
        });
    }

    public void comeIn(String roomId, String memberId, String memberCode) throws Exception {
        // ChatMember chatMember = ChatMember.create(roomId, memberCode);
        // entityStream.of(ChatRoom.class)
        //         .filter(ChatRoom$.ID.eq(roomId)) // ChatRoom$는 자동 생성되는 메타 모델
        //         .forEach(room -> {
        //             repository.save(room);
        //         });

        // Map<Object, Object> chatroom = redis.opsForHash().entries(CHATROOM_KEY_PREFIX
        // + roomId);

        // if (chatroom != null) {
        // String codes = (String) chatroom.get(CHATROOM_PASS_CODES_KEY);
        // List<String> codeList = objectMapper.readValue(codes, new
        // TypeReference<List<String>>() {
        // });

        // if (codeList.contains(code)) {

        // String sessionId2 = (String) redis.opsForValue().get("chatroom:" + roomId +
        // ":presence:" + memberId);

        // if (sessionId2 != null) {
        // // 다중 창 접속임 나중에 처리

        // } else {
        // redis.opsForValue().set("chatroom:" + roomId + ":presence:" + memberId,
        // sessionId);

        // // save that he has a chat for preventing him from reckless creation
        // // once the chat ends, it should be replaced as null
        // redis.opsForHash().put(SessionConstant.SESSION_KEY_PREFIX + sessionId,
        // SessionConstant.SESSION_ROOM_ID_KEY,
        // roomId);
        // }
        // }
        // } else {
        // }

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