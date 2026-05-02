package com.example.stomp.chat.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import com.example.stomp.infra.redis.SlicedRedisSetUp;

@SlicedRedisSetUp
@ContextConfiguration(classes = { ChatService.class })
public class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @TestConfiguration
    static class TesstConfig {
        @Bean
        public com.google.gson.Gson gson() {
            return new com.google.gson.Gson();
        }
    }

    @Test
    void testCreate() throws InterruptedException {
        String id = chatService.create("123", List.of("abcd", "efg"));

        chatService.participate("chatRoom:" + id, "1", "nickname", false);

    }

}
