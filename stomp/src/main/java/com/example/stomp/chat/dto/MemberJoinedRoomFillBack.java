package com.example.stomp.chat.dto;

import java.time.LocalDateTime;

public record MemberJoinedRoomFillBack(String roomUuid, LocalDateTime lastMessagedAt) {
}
