package com.example.stomp.chat.service;

import org.springframework.stereotype.Service;

import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.dto.ChatExceptions;
import com.example.stomp.chat.dto.ChatJoinRequest;
import com.example.stomp.chat.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public String create(String issueTitle) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.create(issueTitle));

        String uuid = chatRoom.getUuid();

        return uuid;
    }

    public void join(ChatJoinRequest joinRequest) {
        ChatRoom chatRoom = chatRoomRepository.findByUuid(joinRequest.roomUUID()).orElseThrow(() -> {
            throw new AppException(ChatExceptions.UNEXISTS_CHAT);
        });

        chatRoom.join(joinRequest.member(), joinRequest.nickname());
    }

    public void get() {
        chatRoomRepository.fetchJoinByUuidWithMembers(roomUUID);

    }

    // public void leave(String roomUUID, Long memberId) {
    // chatRoomRepository.fetchJoinByUuidWithMembers(roomUUID).ifPresent((chatRoom)
    // -> {
    // chatRoom.leave(memberId);
    // });
    // }

}
