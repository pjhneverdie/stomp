package com.example.stomp.trial.dto;

import com.example.stomp.chat.domain.message.MessageType;
import com.example.stomp.trial.dto.ChatMessageSendReq.ChatMsgInfo;
import com.example.stomp.trial.dto.ChatMessageSendReq.SenderInfo;

public record ChatMessageForm(String roomUuid, String content, Long seq, Long chatMemberId, Long memberId,
        MessageType messageType) {

    public ChatMessageSendReq toReq() {
        return ChatMessageSendReq.init(
                new ChatMsgInfo(
                        roomUuid, Long.valueOf(System.currentTimeMillis()), content, seq, messageType),
                new SenderInfo(chatMemberId, memberId));
    }

}
