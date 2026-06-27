package com.example.stomp.feature.chat.domain.message;

import com.example.stomp.entity.BaseEntity;
import com.example.stomp.feature.trial.entity.Trial;
import com.example.stomp.feature.trial.entity.TrialMember;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ChatMessage extends BaseEntity {

    @Column(nullable = false)
    private Long sequence;

    @JoinColumn(name = "chat_room_uuid", referencedColumnName = "chat_room_uuid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Trial chatRoom;

    @JoinColumn(name = "chat_room_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TrialMember sender;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

}
