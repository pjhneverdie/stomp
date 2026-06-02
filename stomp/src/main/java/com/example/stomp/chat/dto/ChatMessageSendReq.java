package com.example.stomp.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChatMessageSendReq {
    private final ChatMsgInfo msgInfo;
    private final SenderInfo senderInfo;

    @Setter
    private RecipientInfo recipientInfo;

    public static record ChatMsgInfo(String roomUuid, Long createdAt, String content, Long seq) {
    }

    @AllArgsConstructor
    @Getter
    public static class SenderInfo {
        private Long memberId;
    }

    @AllArgsConstructor
    @Getter
    public static class RecipientInfo {
        private Long memberId;
    }

    public static ChatMessageSendReq init(ChatMsgInfo msgInfo, SenderInfo senderInfo) {
        return new ChatMessageSendReq(msgInfo, senderInfo, null);
    }
}
