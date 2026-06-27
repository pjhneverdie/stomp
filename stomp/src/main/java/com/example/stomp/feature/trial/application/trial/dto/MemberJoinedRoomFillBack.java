package com.example.stomp.feature.trial.application.trial.dto;

import java.time.LocalDateTime;

public record MemberJoinedRoomFillBack(String roomUuid, LocalDateTime lastMessagedAt) {
}
