package com.example.stomp.chat.dto;

import com.example.stomp.chat.domain.MessageType;
import com.example.stomp.chat.dto.ChatMessageSendReq.ChatMsgInfo;
import com.example.stomp.chat.dto.ChatMessageSendReq.SenderInfo;

public record ChatMessageForm(String roomUuid, String content, Long seq, Long chatMemberId, Long memberId,
        MessageType messageType) {

    public ChatMessageSendReq toReq() {
        return ChatMessageSendReq.init(
                new ChatMsgInfo(
                        roomUuid, Long.valueOf(System.currentTimeMillis()), content, seq, messageType),
                new SenderInfo(chatMemberId, memberId));
    }

}
