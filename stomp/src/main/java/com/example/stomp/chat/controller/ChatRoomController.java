package com.example.stomp.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.chat.dto.form.ChatRoomForm;
import com.example.stomp.chat.service.ChatRoomService;
import com.example.stomp.security.dto.SimpleAuthenticationToken.SimpleMemberDetails;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private ChatRoomService chatRoomService;

    @PostMapping("/create")
    public String create(@RequestBody ChatRoomForm.Create form,
            @AuthenticationPrincipal SimpleMemberDetails memberDetails) {
        return chatRoomService.create(memberDetails.sessionId(), memberDetails.memberId(), form.name(), memberDetails.);
    }

}
