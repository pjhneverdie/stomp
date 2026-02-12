package com.example.stomp.chat.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Document
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChatMessage {

    @Id
    private String id;

    @Indexed
    @NonNull
    private String roomId;

    @NonNull
    private String content;

    @NonNull
    private String senderId;

    @Indexed(sortable = true)
    @NonNull
    private LocalDateTime createdAt;

    public static ChatMessage create(String roomId, String content, String senderId) {
        return new ChatMessage(null, roomId, content, senderId, LocalDateTime.now());
    }

}
