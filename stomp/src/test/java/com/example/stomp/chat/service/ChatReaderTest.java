package com.example.stomp.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.example.stomp.app.infra.redis.config.RedisConfig;
import com.example.stomp.infra.redis.SlicedRedisSetUp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SlicedRedisSetUp
public class ChatReaderTest {

        @Autowired
        private StringRedisTemplate redisTemplate;

        private ChatReader chatReader;

        @BeforeEach
        void setUp() {
                chatReader = new ChatReader(redisTemplate, new ObjectMapper());
                // ZSET
                redisTemplate.opsForZSet().add(
                                "member:1:rooms",
                                "room1",
                                1000);

                redisTemplate.opsForZSet().add(
                                "member:1:rooms",
                                "room2",
                                2000);

                // RedisJSON (raw command)
                redisTemplate.execute((RedisCallback<Object>) connection -> {

                        String json = "[{\"uuid\":\"room1\",\"issueTitle\":\"t1\",\"lastMessage\":\"m1\",\"totalSeq\":10,\"readSeq\":8},"
                                        +
                                        "{\"uuid\":\"room2\",\"issueTitle\":\"t2\",\"lastMessage\":\"m2\",\"totalSeq\":20,\"readSeq\":15}]";

                        connection.execute(
                                        "JSON.SET",
                                        "member:1:roomPreviews".getBytes(),
                                        "$".getBytes(),
                                        json.getBytes());

                        return null;
                });
        }

        @Test
        void testGetChatList() throws JsonMappingException, JsonProcessingException, InterruptedException {
                System.out.println("dsaadsasdsadasdasd");
                System.out.println(chatReader.getChatList(1, 2).size());
                System.out.println("dsaadsasdsadasdasd");
                Thread.sleep(1000000);
        }

}
