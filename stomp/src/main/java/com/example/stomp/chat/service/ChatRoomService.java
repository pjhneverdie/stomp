package com.example.stomp.chat.service;

import org.springframework.stereotype.Service;

import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.domain.ChatRoomMember;
import com.example.stomp.chat.dto.JoinType;
import com.example.stomp.chat.dto.exception.ChatExceptions;
import com.example.stomp.chat.repository.ChatRoomRepo;
import com.example.stomp.member.domain.Member;
import com.example.stomp.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepo chatRoomRepository;
    private final MemberRepository memberRepository;

    public String create(String issueTitle) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.create(issueTitle));

        String uuid = chatRoom.getUuid();

        return uuid;
    }

    public Boolean validateIfJoinable(String uuid, Long memberId) {
        return chatRoomRepository.fetchJoinByUuidWithMembers(uuid).orElseThrow(() -> {
            throw new AppException(ChatExceptions.UNEXISTS_CHAT);
        }).validateIfJoinable(memberId) == JoinType.RECONNECTION;
    }

    public void join(String roomUUID, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findByUuid(roomUUID).get();
        Member member = memberRepository.findById(memberId).get();
        chatRoom.join(member, roomUUID);
    }

}
