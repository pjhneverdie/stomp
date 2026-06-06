package com.example.stomp.chat.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.stomp.chat.dto.SimpleChatMessage;
import com.example.stomp.chat.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private static final long TTL_SECONDS = 7 * 24 * 60 * 60; // 7일

    public void addRecentMessage(String roomUuid, long seq, String messageJson) {
        String key = "chat:%s:recent50".formatted(roomUuid);

        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            byte[] rawKey = key.getBytes(StandardCharsets.UTF_8);

            connection.zAdd(rawKey, seq, messageJson.getBytes());
            connection.keyCommands().expire(rawKey, TTL_SECONDS);
            connection.zSetCommands().zRemRange(rawKey, 0, -51);

            return null;
        });
    }

    public List<SimpleChatMessage> getRecents(String roomUUID, Pageable pageable) {
        return chatMessageRepository.findRecentMessages(roomUUID, pageable);
    }

    public long calUnreads(String roomUuid, LocalDateTime lastVisitedAt) {
        return chatMessageRepository.countUnreadMessages(roomUuid, lastVisitedAt);
    }

}
