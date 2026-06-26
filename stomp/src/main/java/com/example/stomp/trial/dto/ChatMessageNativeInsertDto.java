package com.example.stomp.trial.dto;

import java.time.LocalDateTime;

public record ChatMessageNativeInsertDto(
        String chatRoomUuid,
        Integer chatRoomMemberId,
        Long seq,
        String messageType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer kafkaPartition,
        Long kafkaOffset) {
}
