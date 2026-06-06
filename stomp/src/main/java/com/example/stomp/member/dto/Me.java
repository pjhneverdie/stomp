package com.example.stomp.member.dto;

import java.time.LocalDateTime;

import com.example.stomp.member.domain.Credential;
import com.example.stomp.member.domain.Member;
import com.example.stomp.member.domain.MemberRole;

public record Me(
        String email,
        String picture,
        MemberRole role,
        Integer balance,
        LocalDateTime lastFreeAwardedAt,
        LocalDateTime lastAdAwardedAt) {
    public static Me from(Member member) {
        Credential credential = member.getCredential();
        return new Me(
                member.getEmail(),
                member.getPicture(),
                member.getRole(),
                credential.getBalance(),
                credential.getLastFreeAwardedAt(),
                credential.getLastAdAwardedAt());
    }
}
