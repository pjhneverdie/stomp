package com.example.stomp.chat.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.example.stomp.chat.enum_type.ChatStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@RedisHash
public class ChatRoom {

    @Id
    private String id;

    private String name;

    private List<String> passCodes;

    private ChatStatus status;

    public static ChatRoom create(String id, String name, List<String> passCodes) {
        return new ChatRoom(
                id,
                name,
                passCodes,
                ChatStatus.STEP_APPEALING);
    }

    public boolean isJoinable(String passCode) {
        return this.passCodes.contains(passCode);
    }

}
