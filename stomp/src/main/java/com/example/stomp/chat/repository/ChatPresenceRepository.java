package com.example.stomp.chat.repository;

import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatPresenceRepository {
    private final StringRedisTemplate redisTemplate;

    public void join(String roomId, String userId) {
        redisTemplate.opsForSet().add("chat:participants:" + roomId, userId);
    }

    // get 만들고
    

}
