package com.example.stomp.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.app.dto.ApiResponse;
import com.example.stomp.chat.dto.ChatRoomForm;
import com.example.stomp.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ApiResponse<String> create(@RequestBody ChatRoomForm.Create form) {
        return ApiResponse.createDefaultSuccessResponse(chatRoomService.create(form.name()));
    }

}
