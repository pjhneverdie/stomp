package com.example.stomp.chat.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.stomp.DockerSpringBootTest;

@DockerSpringBootTest
public class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Test
    void dd() {
    }

    @Test
    void testCreate() {
        String id = chatService.create("123", List.of("adsasd", "dsadsa"));

        System.out.println(id);
        System.out.println(id);
        System.out.println(id);

    }

}
