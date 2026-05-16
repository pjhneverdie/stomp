package com.example.stomp.chat.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import com.example.stomp.infra.redis.SlicedRedisSetUp;

@SlicedRedisSetUp

public class ChatServiceTest {

    @TestConfiguration
    static class TesstConfig {
        @Bean
        public com.google.gson.Gson gson() {
            return new com.google.gson.Gson();
        }
    }

    @Test
    void testCreate() throws InterruptedException {

    }

}
