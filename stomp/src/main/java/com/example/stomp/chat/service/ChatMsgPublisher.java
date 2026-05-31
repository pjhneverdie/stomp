package com.example.stomp.chat.service;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgPublisher {

    private final SimpMessageSendingOperations messagingTemplate;

    public void pub() {

    }

}
