package com.example.stomp.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.stomp.chat.domain.ChatRoom;
import java.util.Optional;

public interface ChatRoomRepo extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByUuid(String chatRoomUUID);

}
