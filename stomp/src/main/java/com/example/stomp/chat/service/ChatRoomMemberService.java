package com.example.stomp.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.stomp.chat.domain.ChatRoomMember;
import com.example.stomp.chat.dto.ChatCacheChunk;
import com.example.stomp.chat.dto.ChatCacheChunk.ChatMemberMeta;
import com.example.stomp.chat.repository.ChatMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {

    private final ChatMemberRepository chatMemberRepository;

    public List<String> getJoinedLastestActiveRoomUuiDsById(Long memberId, Integer count) {
        return chatMemberRepository.findChatRoomUuidsByMemberId(memberId);
    }

    // public List<ChatRoomMember> getMeAsChatMember() {
    //     return chatMemberRepository.findAllByMemberIdWithChatRoom(null);
    // }

}
