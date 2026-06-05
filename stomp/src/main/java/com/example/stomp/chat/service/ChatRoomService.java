package com.example.stomp.chat.service;

import org.springframework.stereotype.Service;

import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.domain.ChatTrialStage;
import com.example.stomp.chat.dto.ChatExceptions;
import com.example.stomp.chat.dto.ChatRoomJoinReq;
import com.example.stomp.chat.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public String create(Long memberId, String issueTitle) {
        if (chatRoomRepository.findByMemberIdWithMembers(memberId).stream()
                .filter(cr -> !cr.getTrialStage().equals(ChatTrialStage.JUDGED)).count() >= 1) {
            throw new AppException(ChatExceptions.ONGOING_CHAT_EXISTS);
        }

        return chatRoomRepository.save(ChatRoom.create(issueTitle)).getUuid();
    }

    public ChatRoom join(ChatRoomJoinReq req) {
        ChatRoom chatRoom = chatRoomRepository.findByUuidWithMembers(req.chatRoomUuid())
                .orElseThrow(() -> new AppException(ChatExceptions.UNEXISTS_CHAT));

        chatRoom.join(req.member(), req.nickname());

        return chatRoom;
    }

}
