package com.example.stomp.feature.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.example.stomp.feature.chat.service.ChatMsgProducer;
import com.example.stomp.feature.trial.application.trial.dto.ChatMessageForm;

import lombok.RequiredArgsConstructor;

@Controller
@MessageMapping("/chat")
@RequiredArgsConstructor
public class ChatMsgController {

    private final ChatMsgProducer chatMsgProducer;

    @MessageMapping("/message")
    public void handleMessage(ChatMessageForm form) {
        // chatMsgProducer.sendMessage(form.toReq());
    }

}
