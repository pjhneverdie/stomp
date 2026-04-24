package com.example.stomp.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.app.dto.ApiResponse;
import com.example.stomp.chat.dto.form.ChatRoomForm;
import com.example.stomp.chat.service.ChatService;
import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private ChatService chatRoomService;

    @PostMapping("/create")
    public ApiResponse<String> create(@RequestBody ChatRoomForm.Create form,
            @AuthenticationPrincipal RedisHttpSessionMemberPrincipal principal) {
        return ApiResponse.createDefaultSuccessResponse(
                chatRoomService.create(form.name(), List.<String>of(form.memberCode(), principal.getCode())));
    }

}
