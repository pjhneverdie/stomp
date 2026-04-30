package com.example.stomp.chat.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
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
public class ChatService {

    private final StringRedisTemplate redis;
    private final RedisModulesOperations<String> chatOps;
    private final ChatRoomRepository chatRoomRepository;

    public String create(String name, List<String> passCodes) {

        // 채팅방 생성하고
        // chatMembers까지 다 미리 쳐 넣어 놔.
        // 그리고 나가면 아예 그 멤버에 TTL을 걸어버려
        return chatRoomRepository.save(ChatRoom.create(UUID.randomUUID().toString(), name, passCodes)).getId();

    }

    public ChatRoom orElseThrow(String roomId) {

        return chatRoomRepository.findById(roomId).orElseThrow(() -> {
            throw new AppException(ChatExceptions.UNEXISTS_CHAT);
        });

    }

    public void participate(String roomId, String memberId, String nickname, boolean isReconnect) {

        if (isReconnect) {
            Path2 path = Path2.of(String.format("$.chatMembers[?(@.id == '%s')].networkStatus", memberId));

            chatOps.opsForJSON().set(roomId, NetworkStatus.CONNECTED, path);

            redis.opsForHash().persist("10minutes", List.of(memberId));

            return;
        }

        Path2 path = Path2.of("$.chatMembers");

        chatOps.opsForJSON().arrAppend(roomId, path, ChatMember.create(memberId, nickname));
    }

}