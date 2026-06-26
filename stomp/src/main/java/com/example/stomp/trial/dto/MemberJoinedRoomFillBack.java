package com.example.stomp.trial.dto;

import java.time.LocalDateTime;

public record MemberJoinedRoomFillBack(String roomUuid, LocalDateTime lastMessagedAt) {
}
