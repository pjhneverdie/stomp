package com.example.stomp.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.stomp.chat.dto.SimpleChatMessage;
import com.example.stomp.chat.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public List<SimpleChatMessage> getRecents() {
        return chatMessageRepository.findRecentMessages(null, null);
    }

    public long calUnreads(String roomUUID, Long lastReadMessageId) {
        return chatMessageRepository.countUnread(roomUUID, lastReadMessageId);
    }

}
