package com.example.stomp.chat.service;

import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.example.stomp.infra.redis.SlicedRedisSetUp;

@SlicedRedisSetUp
public class ChatReaderTest {

        @Autowired
        private StringRedisTemplate redisTemplate;

        private ChatReader chatReader;

        @BeforeEach
        void setUp() {

        }
}
