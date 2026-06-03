package com.example.stomp.chat.service;

import org.springframework.stereotype.Service;

import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.dto.ChatCacheReq.ChatRoomMemberCacheReq;
import com.example.stomp.chat.dto.ChatRoomJoinReq;
import com.example.stomp.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatFacade {
    private final MemberService memberService;
    private final ChatRoomService chatRoomService;
    private final ChatCacheService chatCacheService;

    public String create(String issueTitle) {
        return chatRoomService.create(issueTitle);
    }

    public String join(Long memberId, String roomUuid, String nickname) {
        ChatRoom chatRoom = chatRoomService.join(
                new ChatRoomJoinReq(
                        memberService.findByIdOrElseThrow(memberId), roomUuid, nickname));

        chatCacheService.cacheChatRoomMember(
                new ChatRoomMemberCacheReq(
                        String.valueOf(memberId),
                        roomUuid,
                        String.valueOf(chatRoom.getChatRoomMemberById(memberId).getId()),
                        nickname));

        return chatRoom.getUuid();
    }

}
