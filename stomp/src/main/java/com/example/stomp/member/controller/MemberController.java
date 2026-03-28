package com.example.stomp.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stomp.security.dto.SimpleAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/member")
@RestController
public class MemberController {

    @GetMapping("/me")
    public String member(@AuthenticationPrincipal SimpleAuthenticationToken.SimpleMemberDetails memberDetails) {
        return memberDetails.authorities() + " " + memberDetails.memberId();
    }

}
