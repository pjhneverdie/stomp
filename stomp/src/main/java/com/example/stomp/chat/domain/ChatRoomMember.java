package com.example.stomp.chat.domain;

import com.example.stomp.app.domain.BaseEntity;
import com.example.stomp.chat.document.enum_type.MemberTrialStage;
import com.example.stomp.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class ChatRoomMember extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "chat_room_uuid", referencedColumnName = "chat_room_uuid", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MemberTrialStage trialStage;

}
