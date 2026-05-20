package com.example.stomp.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stomp.chat.service.ChatMemberService;
import com.example.stomp.member.dto.Me;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;
    private final ChatMemberService chatRoomMemberService;

    @Transactional(readOnly = true)
    public Me getMe(Long id) {
        return Me.of(memberService.getMemberWithCredentialById(id), chatRoomMemberService.getJoinedRoomUUIDsById(id));
    }

}
