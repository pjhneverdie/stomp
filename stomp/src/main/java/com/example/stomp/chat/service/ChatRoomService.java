package com.example.stomp.chat.service;

import org.springframework.stereotype.Service;

import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.dto.exception.ChatExceptions;
import com.example.stomp.chat.repository.ChatRoomRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepo chatRoomRepository;

    public String create(String issueTitle) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.create(issueTitle));

        String uuid = chatRoom.getUuid();

        return uuid;
    }

    public void isValid(String uuid) {

        ChatRoom chatRoom = chatRoomRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new AppException(ChatExceptions.UNEXISTS_CHAT);
        });


    }

}
