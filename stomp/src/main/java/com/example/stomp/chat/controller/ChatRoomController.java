package com.example.stomp.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.app.dto.ApiResponse;
import com.example.stomp.chat.dto.ChatRoomForm;
import com.example.stomp.chat.service.ChatRoomFacade;
import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private ChatRoomFacade chatRoomFacade;

    @PostMapping("/create")
    public ApiResponse<String> create(
            @RequestBody ChatRoomForm.Create form,
            @AuthenticationPrincipal RedisHttpSessionMemberPrincipal pc) {
        return ApiResponse.createDefaultSuccessResponse(chatRoomFacade.create(pc.getLongId(), form.issueTitle()));
    }

    @PostMapping("/join")
    public ApiResponse<String> create(@RequestBody ChatRoomForm.Join form) {
        return ApiResponse
                .createDefaultSuccessResponse(chatRoomFacade.join(form.memberId(),
                        form.roomUuid(), form.nickname()));
    }

}
