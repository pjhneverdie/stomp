package com.example.stomp.chat.dto;

public record ChatMessageForm() {

    public ChatMessageSendReq toReq() {
        return new ChatMessageSendReq();
    }

}
