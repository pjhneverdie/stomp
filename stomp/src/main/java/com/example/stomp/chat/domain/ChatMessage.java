package com.example.stomp.chat.domain;

import com.example.stomp.app.domain.BaseEntity;

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
    private ChatRoom chatRoom;

    @JoinColumn(name = "chat_room_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatMember sender;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

}
