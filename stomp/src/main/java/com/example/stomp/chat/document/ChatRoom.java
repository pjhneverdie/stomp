package com.example.stomp.chat.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@RedisHash(value = "chatRoom")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChatRoom {

    @Id
    private String id;

    @NonNull
    private String name;

    public static ChatRoom create(String id, String name) {
        return new ChatRoom(id, name);
    }

}
