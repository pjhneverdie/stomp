package com.example.stomp.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.example.stomp.app.infra.websocket.WsMemberPrincipal;
import com.example.stomp.chat.dto.ChatMessageForm;
import com.example.stomp.chat.service.ChatMsgProducer;

import lombok.RequiredArgsConstructor;

@Controller
@MessageMapping("/chat")
@RequiredArgsConstructor
public class ChatMsgController {

    private final ChatMsgProducer chatMsgProducer;

    @MessageMapping("/message")
    public void handleMessage(ChatMessageForm form) {
        chatMsgProducer.sendMessage(form.toReq());
    }

}
