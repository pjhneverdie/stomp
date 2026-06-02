package com.example.stomp.chat.service;

import com.example.stomp.chat.controller.ChatMsgController;
import com.example.stomp.chat.repository.ChatLua;
import com.example.stomp.chat.repository.ChatMemberRepository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.example.stomp.chat.dto.ChatMessageSendReq;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgConsumer {

    private final StringRedisTemplate stringRedisTemplate;
    private final ChatMsgPublisher chatMsgPublisher;
    private final KafkaTemplate<String, ChatMessageSendReq> kafkaTemplate;
    private final ChatLua chatLua;

    private static final int MAX_RECENT_MESSAGES = 50;
    private static final long TTL_SECONDS = 7 * 24 * 60 * 60; // 7일

    public void cacheRecentMessage(String roomUuid, long seq, String messageJson) {
        String key = "chat:%s:recent50".formatted(roomUuid);

        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            byte[] rawKey = key.getBytes(StandardCharsets.UTF_8);

            connection.zAdd(rawKey, seq, messageJson.getBytes());
            connection.keyCommands().expire(rawKey, TTL_SECONDS);
            connection.zSetCommands().zRemRange(rawKey, 0, -51);

            return null;
        });
    }

    public void updatePreviewCard(
            String sMemebrId,
            String rMemebrId,
            String roomUuid,
            String lastActivatedAt,
            String lastMessage,
            String lastMessageSeq) {
        String senderZsetKey = "member:%s:rooms".formatted(sMemebrId);
        String recipientZsetKey = "member:%s:roomPreview:%s".formatted(sMemebrId, roomUuid);
        String senderHashKey = "member:%s:rooms".formatted(rMemebrId);
        String recipientHashKey = "member:%s:roomPreview:%s".formatted(rMemebrId, roomUuid);

        stringRedisTemplate.execute(
                chatLua.previewCardUpdateScript(),
                List.of(senderZsetKey,
                        recipientZsetKey,
                        senderHashKey,
                        recipientHashKey),
                lastActivatedAt,
                roomUuid,
                lastMessage,
                lastMessageSeq);

    }

    @KafkaListener(topics = "chat-send-topic", groupId = "chat-send-topic", concurrency = "20")
    public void prepareAndValidate(List<ChatMessageSendReq> reqList) {

        try {
            // addRecentMessage
        } catch (Exception e) {
            chatMsgPublisher.pubFailure();
        }

        chatMsgPublisher.pubSuccess();

        // CompletableFuture<SendResult<String, List<ChatMessageSendReq>>> future =
        // kafkaTemplate.send(
        // "chat-persist-topic",
        // reqList);

    }

    // 💡 @KafkaListener가 핵심이야!
    @KafkaListener(topics = "chat-persist-topic", groupId = "chat-persist-group", concurrency = "3")
    public void consume(List<ChatMessageSendReq> req) {

    }
}
