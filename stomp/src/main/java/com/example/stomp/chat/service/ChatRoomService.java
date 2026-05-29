package com.example.stomp.chat.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.dto.ChatExceptions;
import com.example.stomp.chat.dto.ChatCacheChunk;
import com.example.stomp.chat.dto.ChatCacheChunk.ChatRoomMeta;
import com.example.stomp.chat.dto.ChatJoinRequest;
import com.example.stomp.chat.repository.ChatRoomMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomMemberRepository chatRoomRepository;

    public String create(String issueTitle) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.create(issueTitle));

        String uuid = chatRoom.getUuid();

        return uuid;
    }

    public void join(ChatJoinRequest joinRequest) {
        ChatRoom chatRoom = chatRoomRepository.fetchJoinByUuidWithMembers(joinRequest.roomUUID()).orElseThrow(() -> {
            throw new AppException(ChatExceptions.UNEXISTS_CHAT);
        });

        chatRoom.join(joinRequest.member(), joinRequest.nickname());
    }

    public ChatRoom getByUUIDWithMembers(String uuid) {
        return chatRoomRepository.fetchJoinByUuidWithMembers(uuid).orElseThrow(() -> {
            throw new AppException(ChatExceptions.UNEXISTS_CHAT);
        });

    }

    public List<ChatRoomMeta> getAllChatRoomMetaByRoomUuid(List<String> roomUuids) {
        return chatRoomRepository.findAllChatRoomMetasByRoomUuid(roomUuids);
    }

    // public void leave(String roomUUID, Long memberId) {
    // chatRoomRepository.fetchJoinByUuidWithMembers(roomUUID).ifPresent((chatRoom)
    // -> {
    // chatRoom.leave(memberId);
    // });
    // }

}
