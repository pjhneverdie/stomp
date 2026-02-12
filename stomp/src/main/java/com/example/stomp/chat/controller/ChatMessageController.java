package com.example.stomp.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.stomp.chat.document.ChatMessage;
import com.example.stomp.chat.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository ChatMessageRepository;

    @MessageMapping("/chat/message")
    public void message(String message) {

        messagingTemplate.convertAndSend("/queue/chat/" + "message.getRoomId()", message);

        ChatMessage chatMessage = ChatMessage.create("d", message , "d");

        ChatMessageRepository.save(chatMessage);

    }

}
