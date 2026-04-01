package com.example.stomp.member.domain;

import java.time.LocalDateTime;

import com.example.stomp.app.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Credential extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false)
    private int balance = 1;

    @Column(nullable = false)
    private LocalDateTime lastFreeAwardedAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime lastAdAwardedAt = LocalDateTime.of(1970, 1, 1, 0, 0);

    public static Credential create(Member member) {
        Credential credential = new Credential();
        credential.member = member;
        return credential;
    }

}