package com.example.stomp.chat.service;

import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatCacheService {

    private final StringRedisTemplate redisTemplate;

    public Long findRecipientMemberId(String sMemberId, String roomUuid) {
        String key = "chat:%s:member".formatted(roomUuid);

        String result = redisTemplate.opsForSet().members(key)
                .stream()
                .filter(m -> !m.equals(sMemberId))
                .findFirst()
                .orElse(null);

        return result != null ? Long.valueOf(result) : null;
    }

}
