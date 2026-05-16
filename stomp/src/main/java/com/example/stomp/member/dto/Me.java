package com.example.stomp.member.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.stomp.member.domain.Credential;
import com.example.stomp.member.domain.Member;
import com.example.stomp.member.domain.MemberRole;

public record Me(
        String email,
        String picture,
        MemberRole role,
        Integer balance,
        LocalDateTime lastFreeAwardedAt,
        LocalDateTime lastAdAwardedAt,
        List<String> joinedRoomUUIDs) {
    public static Me of(Member member, List<String> joinedRoomUUIDs) {
        Credential credential = member.getCredential();
        return new Me(
                member.getEmail(),
                member.getPicture(),
                member.getRole(),
                credential.getBalance(),
                credential.getLastFreeAwardedAt(),
                credential.getLastAdAwardedAt(),
                joinedRoomUUIDs);
    }
}
