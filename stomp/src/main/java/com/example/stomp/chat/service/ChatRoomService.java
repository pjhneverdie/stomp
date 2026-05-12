package com.example.stomp.chat.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.stomp.app.constant.SessionConstant;
import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.domain.ChatRoomMember;
import com.example.stomp.chat.dto.JoinType;
import com.example.stomp.chat.dto.exception.ChatExceptions;
import com.example.stomp.chat.repository.ChatRoomRepository;
import com.example.stomp.member.domain.Member;
import com.example.stomp.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public String create(String issueTitle) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.create(issueTitle));

        String uuid = chatRoom.getUuid();

        return uuid;
    }

    public Boolean validateIfJoinable(String roomUUID, Long memberId) {
        return chatRoomRepository.fetchJoinByUuidWithMembers(roomUUID).orElseThrow(() -> {
            throw new AppException(ChatExceptions.UNEXISTS_CHAT);
        }).validateIfJoinable(memberId) == JoinType.RECONNECTION;
    }

    public void join(String roomUUID, Long memberId, String nickname) {
        ChatRoom chatRoom = chatRoomRepository.findByUuid(roomUUID).orElseThrow(() -> {
            throw new AppException(ChatExceptions.UNEXISTS_CHAT);
        });

        Member member = memberRepository.findById(memberId).get();

        chatRoom.join(member, nickname);
    }

    public void leave(String roomUUID, Long memberId) {
        chatRoomRepository.fetchJoinByUuidWithMembers(roomUUID).ifPresent((chatRoom) -> {
            chatRoom.leave(memberId);
        });
    }

}
