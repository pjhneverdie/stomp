package com.example.stomp.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stomp.member.dto.Me;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;

    @Transactional(readOnly = true)
    public Me getMe(Long id) {
        return Me.from(memberService.getMemberWithCredentialById(id));
    }

}
