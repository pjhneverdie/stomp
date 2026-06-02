package com.example.stomp.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.example.stomp.chat.dto.ChatMessageForm;
import com.example.stomp.chat.service.ChatMsgProducer;

import lombok.RequiredArgsConstructor;

@Controller
@MessageMapping("/chat")
@RequiredArgsConstructor
public class ChatMsgController {

    private final ChatMsgProducer chatProducer;

    @MessageMapping("/message")
    public void handleMessage(ChatMessageForm form) {
        chatProducer.sendMessage(form.toReq());

    }

}
