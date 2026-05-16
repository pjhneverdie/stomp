package com.example.stomp.member.domain;

import java.time.LocalDateTime;

import com.example.stomp.app.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Credential extends BaseEntity {

    @Column(nullable = false)
    private Integer balance;

    @Column(nullable = false)
    private LocalDateTime lastFreeAwardedAt;

    @Column(nullable = false)
    private LocalDateTime lastAdAwardedAt;

    public static Credential create() {
        return new Credential(1, LocalDateTime.now(), LocalDateTime.of(1970, 1, 1, 0, 0));
    }

}