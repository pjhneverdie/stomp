package com.example.stomp.chat.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.util.Assert;

import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.document.enum_type.ChatChapter;
import com.example.stomp.chat.document.enum_type.NetworkStatus;
import com.example.stomp.chat.dto.JoinType;
import com.example.stomp.chat.dto.exception.ChatExceptions;
import com.redis.om.spring.annotations.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document("chatRoom")
public class ChatRoom {

    @Id
    private String id;

    private String name;

    private List<String> passCodes;

    private List<Participant> participants;

    private ChatChapter chapter;

    public static ChatRoom create(String id, String name, List<String> passCodes) {
        return new ChatRoom(
                id,
                name,
                passCodes,
                new ArrayList<>(),
                ChatChapter.STAND_BY);
    }

    public void validatePassCode(String passCode) {
        Assert.isTrue(this.passCodes.contains(passCode), () -> {
            throw new AppException(ChatExceptions.UNMATCHABLE_MEMBER_CODE);
        });
    }

    public JoinType validateSession(String memberId) {
        Optional<Participant> participantOp = participants.stream()
                .filter(p -> p.getId().equals(memberId))
                .findFirst();

        if (participantOp.isPresent()) {
            Assert.isTrue(participantOp.get().getNetworkStatus() != NetworkStatus.CONNECTED, () -> {
                throw new AppException(ChatExceptions.MULTIPLE_WS_SESSION_DETECTED);
            });

            return JoinType.RECONNECTION;
        }

        return JoinType.FIRST_MET;
    }

}
