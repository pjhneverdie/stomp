package com.example.stomp.app.constant;

public final class RedisConstant {

    private RedisConstant() {
    }

    public static String memberRooms(Long memberId) {
        return "member:%d:rooms".formatted(memberId);
    }

    public static String roomPreview(Long memberId, String roomUuid) {
        return "member:%d:roomPreview:%s".formatted(memberId, roomUuid);
    }

    public static String ROOM_PREVIEW_HFKEY_UUID = "uuid";
    public static String ROOM_PREVIEW_HFKEY_ISSUE_TITLE = "issueTitle";
    public static String ROOM_PREVIEW_HFKEY_LAST_MESSAGE = "lastMessage";
    public static String ROOM_PREVIEW_HFKEY_TOTAL_COUNT = "totalSeqCount";
    public static String ROOM_PREVIEW_HFKEY_READ_COUNT = "readSeqCount";

}
