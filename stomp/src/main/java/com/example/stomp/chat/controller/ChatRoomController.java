package com.example.stomp.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.chat.dto.form.ChatRoomForm;
import com.example.stomp.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private ChatRoomService chatRoomService;

    @PostMapping("/create")
    public String create(@RequestBody ChatRoomForm.Create form) {
        String roomId = UUID.randomUUID().toString();

        chatRoomService.create(roomId, form.name());

        return roomId;
    }

}
