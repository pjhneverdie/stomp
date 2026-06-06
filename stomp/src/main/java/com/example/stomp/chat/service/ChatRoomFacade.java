package com.example.stomp.chat.service;

import org.springframework.stereotype.Service;

import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.repository.ChatCacheService;
import com.example.stomp.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomFacade {
    private final MemberService memberService;
    private final ChatRoomService chatRoomService;
    private final ChatCacheService chatCacheService;

    public String create(Long memberId, String issueTitle) {
        return chatRoomService.create(memberId, issueTitle);
    }

    public String join(Long memberId, String roomUuid, String nickname) {
        ChatRoom chatRoom = chatRoomService.findByUuidWithMembers(roomUuid);

        chatRoom.join(memberService.findById(memberId), nickname);

        chatCacheService.cacheJoinedMemberInfo(
                String.valueOf(memberId),
                roomUuid,
                String.valueOf(
                        chatRoom.getChatRoomMemberById(memberId).getId()),
                nickname);

        return chatRoom.getUuid();
    }

}
