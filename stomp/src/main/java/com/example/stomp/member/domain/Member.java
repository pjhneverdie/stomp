package com.example.stomp.member.domain;

import com.example.stomp.member.enum_type.MemberRole;
import com.example.stomp.shared.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false)
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MemberRole memberRole = MemberRole.FREE;

    public static Member createMember(String email, String picture) {
        return new Member(email, picture, MemberRole.FREE);
    }

}
