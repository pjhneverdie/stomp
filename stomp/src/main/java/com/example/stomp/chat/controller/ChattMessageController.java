package com.example.stomp.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChattMessageController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(String message) {
      
        messagingTemplate.convertAndSend("/queue/chat/" + "message.getRoomId()", message);
    }

}
