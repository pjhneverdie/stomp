package com.example.stomp.feature.trial.application.trial.dto;

public record ChatCacheReq() {

    public static record ChatRoomMemberCacheReq(String memberId, String roomUuid, String chatRoomMemberId,
            String nickname) {
    }

}
