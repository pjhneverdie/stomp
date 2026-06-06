package com.example.stomp.chat.document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import com.example.stomp.app.constant.RedisKeys;

public record RoomPreview(
                String uuid,
                String issueTitle,
                String lastMessage,
                LocalDateTime lastMessagedAt,
                Long unReadCount) {
        public static String PREVIEW_DTO_MAP_LAST_MESSAGED_KEY = "lastMessagedAt";

        // public static RoomPreview from(
        //                 Map<String, String> map) {
        //         Long totalSeq = Long.parseLong(map.get(RedisKeys.ROOM_PREVIEW_HFKEY_TOTAL_COUNT));
        //         Long readSeq = Long.parseLong(map.get(RedisKeys.ROOM_PREVIEW_HFKEY_READ_COUNT));
        //         Long unReadCount = totalSeq - readSeq;

        //         Long lastMessagedAtMillis = Long.parseLong(map.get(PREVIEW_DTO_MAP_LAST_MESSAGED_KEY));
        //         LocalDateTime lastMessagedAt = Instant.ofEpochMilli(lastMessagedAtMillis)
        //                         .atZone(ZoneId.systemDefault())
        //                         .toLocalDateTime();

        //         return new RoomPreview(
        //                         map.get(RedisKeys.ROOM_PREVIEW_HFKEY_UUID),
        //                         map.get(RedisKeys.ROOM_PREVIEW_HFKEY_ISSUE_TITLE),
        //                         map.get(RedisKeys.ROOM_PREVIEW_HFKEY_LAST_MESSAGE),
        //                         lastMessagedAt,
        //                         unReadCount);
        // }
}