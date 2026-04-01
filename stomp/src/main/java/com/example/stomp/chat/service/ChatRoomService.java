package com.example.stomp.chat.service;

import org.springframework.stereotype.Service;

import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public void create(String roomId, String name) {
        // 혹시 이미 채팅에 참여중인 건 아닌지 확인
        chatRoomRepository.save(ChatRoom.create(roomId, name));
    }

}
