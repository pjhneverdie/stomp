package com.example.stomp.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.stomp.chat.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}