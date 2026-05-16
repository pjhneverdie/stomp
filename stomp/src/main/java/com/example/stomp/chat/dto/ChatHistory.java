package com.example.stomp.chat.dto;

import java.util.List;

import com.example.stomp.chat.domain.ChatTrialStage;

// chatroom:id

// chatroom:crid:members

// 이건 응답으로 갈 거야.
public class ChatHistory {

    public String chatUUID;

    public String issueTitle;

    public ChatTrialStage trialStage;

    public List<SimpleChatMessageDto> SimpleChatMessageDto;

    public Integer unReadCount;

}
