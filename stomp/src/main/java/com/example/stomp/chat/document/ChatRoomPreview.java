package com.example.stomp.chat.document;

import java.time.LocalDateTime;
import java.util.Map;

public record ChatRoomPreview(
                String uuid,
                String issueTitle,
                String lastMessage,
                LocalDateTime lastMessagedAt,
                Long unReadCount) {

        public static ChatRoomPreview from(Map<String, String> map) {

                Long totalSeq = Long.parseLong(map.get("totalSeq"));
                Long readSeq = Long.parseLong(map.get("readSeq"));

                return new ChatRoomPreview(
                                map.get("uuid"),
                                map.get("title"),
                                map.get("lastMessage"),
                                LocalDateTime.parse(map.get("lastMessagedAt")),
                                totalSeq - readSeq);
        }

}