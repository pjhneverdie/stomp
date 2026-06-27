package com.example.stomp.feature.chat.service;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.example.stomp.feature.trial.application.trial.dto.ChatMessageSendReq.SenderInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgPublisher {

    // private final SimpMessageSendingOperations messagingTemplate;

    // public void pubFailure(SenderInfo senderInfo) {
    //     messagingTemplate.convertAndSendToUser(String.valueOf(senderInfo.memberId()), "/queue/messages", "");

    // }

    // public void pubSuccess() {

    // }

}
