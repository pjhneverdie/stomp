package com.example.stomp.chat.repository;

import org.springframework.stereotype.Repository;

import com.example.stomp.chat.document.ChatRoom;
import com.redis.om.spring.repository.RedisDocumentRepository;

@Repository
public interface ChatRoomRepository extends RedisDocumentRepository<ChatRoom, String> {
}