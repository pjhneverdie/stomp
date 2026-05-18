package com.example.stomp.chat.dto;

import java.util.List;

import com.example.stomp.chat.domain.ChatRoom;

public record ChatInitialView(
                String roomUUID,
                String issueTitle,
                List<SimpleChatMember> chatMembers,
                List<SimpleChatMessage> chatMessages,
                Integer myUnReadCount) {

        public record SimpleChatMember(
                        Long memberId,
                        String nickname) {
        }

        public static ChatInitialView of(
                        ChatRoom chatRoom,
                        List<SimpleChatMessage> chatMessages, Integer myUnReadCount) {

                List<SimpleChatMember> members = chatRoom.getMembers()
                                .stream()
                                .map(chatMember -> new SimpleChatMember(
                                                chatMember.getId(),
                                                chatMember.getNickname()))
                                .toList();

                return new ChatInitialView(
                                chatRoom.getUuid(),
                                chatRoom.getIssueTitle(),
                                members,
                                chatMessages,
                                myUnReadCount);
        }
}