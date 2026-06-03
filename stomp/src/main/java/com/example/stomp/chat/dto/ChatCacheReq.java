package com.example.stomp.chat.dto;

public record ChatCacheReq() {

    public static record ChatRoomMemberCacheReq(String memberId, String roomUuid, String chatRoomMemberId,
            String nickname) {
    }

}
