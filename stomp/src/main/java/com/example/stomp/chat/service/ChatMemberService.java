package com.example.stomp.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.stomp.chat.domain.ChatMember;
import com.example.stomp.chat.dto.ChatCacheChunk;
import com.example.stomp.chat.dto.ChatCacheChunk.ChatMemberMeta;
import com.example.stomp.chat.repository.ChatMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMemberService {

    private final ChatMemberRepository chatMemberRepository;

    public List<String> getJoinedLastestActiveRoomUuiDsById(Long memberId, Integer count) {
        return chatMemberRepository.findChatRoomUuidsByMemberId(memberId);
    }

    public List<ChatMember> getMeAsChatMember() {
        return chatMemberRepository.findAllByMemberIdWithChatRoom(null);
    }


}
