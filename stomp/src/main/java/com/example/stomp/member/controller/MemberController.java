package com.example.stomp.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.security.dto.RedisHttpSessionAuthenticationToken;
import com.example.stomp.security.dto.RedisHttpSessionMemberPrincipal;

import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping("/member")
@RestController
public class MemberController {

    @GetMapping("/me")
    public String member(@AuthenticationPrincipal RedisHttpSessionMemberPrincipal principal) {
        return principal.getAuthorities() + " " + principal.getId();
    }

}
