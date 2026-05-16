package com.example.stomp.chat.dto;

import java.time.LocalDateTime;

import com.example.stomp.chat.domain.MessageType;

public record SimpleChatMessageDto(
        // It is not the same as 'senderId', 'senderId' means the id of ChatRoomMember.
        Long memberId,
        String nickname,
        String content,
        MessageType messageType,
        LocalDateTime createdAt) {
}
