package com.example.stomp.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.application.dto.ApiResponse;
import com.example.stomp.member.dto.Me;
import com.example.stomp.member.service.MemberFacade;
import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping("/member")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberFacade memberFacade;

    @GetMapping("/me")
    public ApiResponse<Me> me(@AuthenticationPrincipal RedisHttpSessionMemberPrincipal principal) {
        return ApiResponse.createDefaultSuccessResponse(memberFacade.getMe(principal.getLongId()));
    }

}
