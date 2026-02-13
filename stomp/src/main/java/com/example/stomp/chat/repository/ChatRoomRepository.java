package com.example.stomp.chat.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.stomp.chat.document.ChatRoom;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, String> {

}
