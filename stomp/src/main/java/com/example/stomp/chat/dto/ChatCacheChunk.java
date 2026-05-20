package com.example.stomp.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.domain.ChatTrialStage;
import com.example.stomp.chat.domain.PersonelTrialStage;

public record ChatCacheChunk(
                ChatRoomMeta chatMeta,
                List<ChatMemberMeta> chatMembers) {

        public static record ChatRoomMeta(
                        String roomUuid,
                        String issueTitle,
                        ChatTrialStage chatTrialStage,
                        Long totalSequence,
                        String lastMessage,
                        LocalDateTime lastActivedAt) {
        }

        public static record ChatMemberMeta(
                        String roomUuid,
                        Long memberId, // The memberId of the sender.
                        Long senderId, // The id member uses in the chatroom.
                        String nickname,
                        PersonelTrialStage personelTrialStage,
                        Long readSequence) {
        }

}