package com.example.stomp.trial.dto;

import java.time.LocalDateTime;

import com.example.stomp.chat.domain.message.MessageType;

public record SimpleChatMessage(
        Long id,
        Long senderId,
        String content,
        MessageType messageType,
        Long createdAt) {
}