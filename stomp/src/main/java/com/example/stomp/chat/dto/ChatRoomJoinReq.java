package com.example.stomp.chat.dto;

import com.example.stomp.member.domain.Member;

public record ChatRoomJoinReq(Member member, String roomUuid, String nickname) {
}
