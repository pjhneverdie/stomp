package com.example.stomp.chat.domain;

import com.example.stomp.app.domain.BaseEntity;
import com.example.stomp.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "chatroom_participant")
public class ChatRoomParticipant extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String roomId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    public static ChatRoomParticipant participate(String roomId, Member member) {
        return new ChatRoomParticipant(roomId, member);
    }

}
