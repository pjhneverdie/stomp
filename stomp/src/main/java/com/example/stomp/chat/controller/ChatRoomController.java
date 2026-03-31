package com.example.stomp.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.dto.form.ChatRoomForm;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    @PostMapping("/create")
    public String createChatRoom(@RequestBody ChatRoomForm.Create form) {
        String roomId = UUID.randomUUID().toString();

        ChatRoom chatRoom = ChatRoom.create(roomId, form.name());

        return roomId;
    }

}
