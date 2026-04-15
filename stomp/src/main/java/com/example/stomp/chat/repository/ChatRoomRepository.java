package com.example.stomp.chat.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.stomp.chat.domain.ChatRoom;

@Repository
public interface ChatRoomRepository extends CrudRepository<ChatRoom, String> {
}