package com.example.stomp.chat.service;

import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatReader {

    private final Chat
    private final StringRedisTemplate redis;

    public void dd() {
        // 1. redis에 chatroom:id로 검색
        // 2. 없으면 mysql에서 fetch

        redis.opsForHash().entries("chatroom:" + "uuid");
        //


    }

}
