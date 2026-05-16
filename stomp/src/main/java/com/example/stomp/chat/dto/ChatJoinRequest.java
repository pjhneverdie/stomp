package com.example.stomp.chat.dto;

import com.example.stomp.member.domain.Member;

public record ChatJoinRequest(String roomUUID, Member member, String nickname) {
}
