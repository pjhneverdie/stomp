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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom extends BaseEntity {

    private static final int DEFAULT_MAX_CAPACITY = 2;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, unique = true, length = 36)
    private String roomId;

    @Column(nullable = false, updatable = false)
    private int maxCapacity;

    public static ChatRoom create(String name, String roomId) {
        return new ChatRoom(name, roomId, DEFAULT_MAX_CAPACITY);
    }

}
