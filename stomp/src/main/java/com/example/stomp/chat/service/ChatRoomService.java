package com.example.stomp.chat.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.stomp.app.dto.AppException;
import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.domain.ChatTrialStage;
import com.example.stomp.chat.dto.ChatExceptions;
import com.example.stomp.chat.repository.ChatRoomRepository;
import com.example.stomp.member.domain.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ApplicationEventPublisher eventPublisher;
    private final ChatRoomRepository chatRoomRepository;

    public String create(Member member, String issueTitle, String nickname) {
        if (chatRoomRepository.countUnTerminatedTrialByMemberId(member.getId(), ChatTrialStage.JUDGED) > 0) {
            throw new AppException(ChatExceptions.ONGOING_CHAT_EXISTS);
        }

        return chatRoomRepository.save(ChatRoom.create(member, issueTitle, nickname)).getUuid();
    }

    public ChatRoom findByUuidWithMembers(String roomUuid) {
        return chatRoomRepository.findByUuidWithMembers(roomUuid)
                .orElseThrow(() -> new AppException(ChatExceptions.UNEXISTS_CHAT));
    }

}
