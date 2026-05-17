package com.example.stomp.chat.dto;

import java.time.LocalDateTime;

import com.example.stomp.chat.domain.MessageType;

public record ChatMessageView(
        Long id,
        Long senderId,
        String content,
        MessageType messageType,
        LocalDateTime createdAt) {
}