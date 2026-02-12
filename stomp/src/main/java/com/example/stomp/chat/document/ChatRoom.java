package com.example.stomp.chat.document;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Document
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
