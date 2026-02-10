package com.example.stomp.chat.repository;

import com.example.stomp.chat.document.ChatRoom;

import com.redis.om.spring.repository.RedisDocumentRepository;

public interface ChatRoomRepository extends RedisDocumentRepository<ChatRoom, String> {

}
