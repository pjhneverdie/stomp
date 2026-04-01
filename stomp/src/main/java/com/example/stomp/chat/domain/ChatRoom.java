package com.example.stomp.chat.domain;

import com.example.stomp.app.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Column(nullable = false, updatable = false, unique = true, length = 36)
    private String roomId;

    @Column(nullable = false, updatable = false, length = 30)
    private String name;

    @Column(nullable = false, updatable = false)
    private int maxCapacity = 2;

    public static ChatRoom create(String roomId, String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = roomId;
        chatRoom.name = name;
        return chatRoom;
    }

}
