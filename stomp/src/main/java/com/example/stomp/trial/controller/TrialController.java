package com.example.stomp.trial.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.application.dto.ApiResponse;
import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;
import com.example.stomp.trial.dto.ChatRoomForm;
import com.example.stomp.trial.facade.TrialFacade;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class TrialController {

    private TrialFacade chatRoomFacade;

    @PostMapping("/create")
    public ApiResponse<String> create(
            @RequestBody ChatRoomForm.Create form,
            @AuthenticationPrincipal RedisHttpSessionMemberPrincipal pc) {
        return ApiResponse.createDefaultSuccessResponse(
                chatRoomFacade.create(pc.getLongId(), form.issueTitle(), form.nickname()));
    }

    @PostMapping("/join")
    public ApiResponse<String> create(@RequestBody ChatRoomForm.Join form) {
        return ApiResponse
                .createDefaultSuccessResponse(chatRoomFacade.join(form.memberId(),
                        form.roomUuid(), form.nickname()));
    }

}
