package com.example.stomp.feature.chat.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.stomp.feature.chat.domain.message.ChatMessage;
import com.example.stomp.feature.chat.infrastructure.ChatCacheService;
import com.example.stomp.feature.trial.application.trial.dto.ChatMessageSendReq;
import com.example.stomp.feature.trial.application.trial.dto.ChatMessageSendReq.RecipientInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgProducer {

    // private final ChatCacheService cacheService;

    // private final ChatMsgPublisher chatMsgPublisher;

    // private final KafkaTemplate<String, ChatMessageSendReq> kafkaTemplate;

    // public void sendMessage(ChatMessageSendReq req) {
    //     try {
    //         // 1. Find recipient to send.
    //         cacheService.findRecipientInfo(req).ifPresent((info) -> {
    //             req.setRecipientInfo(info);
    //         });
    //     } catch (Exception e) {
    //         chatMsgPublisher.pubFailure(req.getSenderInfo());
    //     }

    //     // 2. Produce for caching & sending
    //     CompletableFuture<SendResult<String, ChatMessageSendReq>> future = kafkaTemplate.send(
    //             "chat-send-topic",
    //             req);

    //     // 3. If producing fails, publish fail ack to sender.
    //     future.whenComplete((result, ex) -> {
    //         if (ex == null) {
    //             System.out.println("카프카에 저장 성공, 오프셋: " + result.getRecordMetadata().offset());
    //         } else {
    //             System.err.println("카프카 전송 실패! 원인: " + ex.getMessage());

    //             chatMsgPublisher.pubFailure(req.getSenderInfo());
    //         }
    //     });
    // }

}
