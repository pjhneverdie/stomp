package com.example.stomp.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.example.stomp.chat.infrastructure.ChatCacheService;
import com.example.stomp.trial.dto.ChatMessageSendReq;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgConsumer {

    // private final ChatCacheService chatCacheService;
    // private final ChatMsgPublisher chatMsgPublisher;

    // @KafkaListener(topics = "chat-outbox-topic", groupId = "chat-outbox-topic", concurrency = "20")
    // public void updatePublishProduce(List<ChatMessageSendReq> reqs,
    //         @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
    //         @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    //     try {
    //         List<Object> results = chatCacheService.updatePersonelViewAndRecentMessageBulk(reqs);

    //         for (int i = 0; i < reqs.size(); i++) {
    //             ChatMessageSendReq req = reqs.get(i);
    //             Object result = results.get(i);

    //             if (result != null && !(result instanceof Exception)) {
    //                 chatMsgPublisher.pubSuccess();
    //             } else {
    //                 chatMsgPublisher.pubFailure(req.getSenderInfo());
    //             }
    //         }
    //     } catch (Exception e) {
    //     }

    // }

}
