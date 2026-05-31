package com.example.stomp.chat.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.stomp.chat.domain.ChatMessage;
import com.example.stomp.chat.dto.ChatMessageSendReq;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgProducer {

    private final KafkaTemplate<String, ChatMessageSendReq> kafkaTemplate;

    private final ChatMsgPublisher chatMsgPublisher;

    public void sendMessage(ChatMessageSendReq req) {
        CompletableFuture<SendResult<String, ChatMessageSendReq>> future = kafkaTemplate.send("chat-topic", req);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("카프카에 저장 성공, 오프셋: " + result.getRecordMetadata().offset());
            } else {
                System.err.println("카프카 전송 실패! 원인: " + ex.getMessage());

                chatMsgPublisher.pub();
            }
        });
    }

}
