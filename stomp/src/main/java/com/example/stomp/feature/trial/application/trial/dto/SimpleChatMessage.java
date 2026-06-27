package com.example.stomp.feature.trial.application.trial.dto;

import java.time.LocalDateTime;

import com.example.stomp.feature.chat.domain.message.MessageType;

public record SimpleChatMessage(
        Long id,
        Long senderId,
        String content,
        MessageType messageType,
        Long createdAt) {
}