package com.example.stomp.chat.repository;

import com.example.stomp.chat.document.ChatMessage;
import com.redis.om.spring.repository.RedisDocumentRepository;

public interface ChatMessageRepository extends RedisDocumentRepository<ChatMessage, String> {

}
