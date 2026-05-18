package com.example.stomp.chat.service;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.dto.ChatInitialView;
import com.example.stomp.chat.dto.SimpleChatMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatReader {

    // private final ChatMessage
    private final StringRedisTemplate redis;

    private final ChatRoomService chatRoomService;

    private final ChatMessageService chatMessageService;

    public void dd() {
        // 1. redis에 chatroom:id로 검색
        // 2. 없으면 mysql에서 fetch

        redis.opsForHash().entries("chatroom:" + "uuid");
        //

        ChatRoom chatRoom = chatRoomService.getByUUID("uuid");
        List<SimpleChatMessage> chatMessages = chatMessageService.getRecents();
        chatMessageService.calUnreads(chatRoom.getMembers().stream().filter((chatMember)-> {
            return chatMember.getId() == 
        }));

        ChatInitialView chatInitialView = ChatInitialView.of(chatRoom, chatMessageView, null);

    }

}
