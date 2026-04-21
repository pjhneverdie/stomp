package com.example.stomp.chat.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.document.ChatMember;
import com.example.stomp.chat.document.ChatRoom;
import com.example.stomp.chat.document.enum_type.NetworkStatus;
import com.example.stomp.chat.dto.exception.ChatExceptions;
import com.example.stomp.chat.repository.ChatRoomRepository;

import com.redis.om.spring.ops.RedisModulesOperations;

import lombok.RequiredArgsConstructor;

import redis.clients.jedis.json.Path2;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final RedisTemplate<String, Object> redis;
    private final RedisModulesOperations<String> chatOps;
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

    public void participate(String roomId, String memberId, boolean isReconnect) {
        if (isReconnect) {
            Path2 path = Path2.of(String.format("$.chatMembers[?(@.id == '%s')].networkStatus", memberId));

            chatOps.opsForJSON().set(roomId, NetworkStatus.CONNECTED, path);

            return;
        }

        Path2 path = Path2.of("$.chatMembers");

        chatOps.opsForJSON().arrAppend(roomId, path, ChatMember.create(memberId, "NICK"));
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