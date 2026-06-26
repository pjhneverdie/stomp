package com.example.stomp.application.constant;

public final class RedisKeys {

    private RedisKeys() {
    }

    public static String chatMessageOutbox() {
        return "outbox:chat_message";
    }

    public static String recent50(String roomUuid) {
        return "chat_room:%s:recent50".formatted(roomUuid);
    }

    public static String memberRooms(String memberId) {
        return "member:%s:rooms".formatted(memberId);
    }

    public static String memberRoomPreview(String memberId, String roomUuid) {
        return "member:%s:room_preview:%s".formatted(memberId, roomUuid);
    }

    public static String roomMembers(String roomUuid) {
        return "chat_room:%s:members".formatted(roomUuid);
    }

    public static String roomMember(String roomUuid, String chatRoomMemberId) {
        return "chat_room:%s:member:%s".formatted(roomUuid, chatRoomMemberId);
    }

    public static final String ROOM_MEMBER_HFKEY_MEMBER_ID = "member_id";
    public static final String ROOM_MEMBER_HFKEY_NICKNAME = "nickname";

}
