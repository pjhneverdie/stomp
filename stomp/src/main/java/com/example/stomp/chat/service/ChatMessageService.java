package com.example.stomp.chat.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.stomp.chat.dto.SimpleChatMessage;
import com.example.stomp.chat.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public List<SimpleChatMessage> getRecents(String roomUUID, Pageable pageable) {
        return chatMessageRepository.findRecentMessages(roomUUID, pageable);
    }

    public long calUnreads(String roomUuid, LocalDateTime lastVisitedAt) {
        return chatMessageRepository.countUnreadMessages(roomUuid, lastVisitedAt);
    }

}
