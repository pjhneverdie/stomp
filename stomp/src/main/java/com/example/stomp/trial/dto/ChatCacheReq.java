package com.example.stomp.trial.dto;

public record ChatCacheReq() {

    public static record ChatRoomMemberCacheReq(String memberId, String roomUuid, String chatRoomMemberId,
            String nickname) {
    }

}
