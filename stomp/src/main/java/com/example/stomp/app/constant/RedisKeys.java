package com.example.stomp.app.constant;

public final class RedisKeys {

    private RedisKeys() {
    }

    public static String recent50(String roomUuid) {
        return "chat:%s:recent50".formatted(roomUuid);
    }

    public static String memberRooms(String memberId) {
        return "member:%s:rooms".formatted(memberId);
    }

    public static String memberRoomPreview(String memberId, String roomUuid) {
        return "member:%s:room_preview:%s".formatted(memberId, roomUuid);
    }

    // room members (set)
    public static String roomMembers(String roomUuid) {
        return "room:%s:members".formatted(roomUuid);
    }

    // room member (hash)
    public static String roomMember(String roomUuid, String chatRoomMemberId) {
        return "room:%s:member:%s".formatted(roomUuid, chatRoomMemberId);
    }

    // room member hash fields
    public static final String ROOM_MEMBER_HFKEY_MEMBER_ID = "member_id";
    public static final String ROOM_MEMBER_HFKEY_NICKNAME = "nickname";

}
