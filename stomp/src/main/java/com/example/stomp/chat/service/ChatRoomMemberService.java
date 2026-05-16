package com.example.stomp.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.stomp.chat.repository.ChatMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {

    private final ChatMemberRepository chatRoomMemberRepository;

    public List<String> getJoinedRoomUUIDsById(Long memberId) {
        return chatRoomMemberRepository.findChatRoomUuidsByMemberId(memberId);
    }

}
