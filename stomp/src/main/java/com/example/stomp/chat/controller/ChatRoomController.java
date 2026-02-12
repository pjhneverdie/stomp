package com.example.stomp.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.chat.document.ChatRoom;
import com.example.stomp.chat.dto.form.ChatRoomForm;
import com.example.stomp.chat.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    @PostMapping("/create")
    public String createChatRoom(@RequestBody ChatRoomForm.Create form) {
        String roomId = UUID.randomUUID().toString().replace("-", "");

        ChatRoom chatRoom = ChatRoom.create(roomId, form.name());

        chatRoomRepository.save(chatRoom);

        return roomId;
    }

}
