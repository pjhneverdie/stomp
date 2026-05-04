package com.example.stomp.chat.domain;

import java.util.UUID;

import com.example.stomp.app.domain.BaseEntity;
import com.example.stomp.chat.document.enum_type.ChatTrialStage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChatRoom extends BaseEntity {

    @Column(name = "chat_room_uuid", nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(nullable = false, length = 50)
    private String issueTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChatTrialStage trialStage;

    public static ChatRoom create(String issueTitle) {
        return new ChatRoom(UUID.randomUUID().toString(), issueTitle, ChatTrialStage.STAND_BY);
    }

}
