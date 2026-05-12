package com.example.stomp.chat.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.stomp.app.domain.BaseEntity;
import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.document.enum_type.ChatTrialStage;
import com.example.stomp.chat.dto.JoinType;
import com.example.stomp.chat.dto.exception.ChatExceptions;
import com.example.stomp.member.domain.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChatRoom extends BaseEntity {

    @Column(name = "chat_room_uuid", nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(nullable = false, length = 50)
    private String issueTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChatTrialStage trialStage;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomMember> members;

    public static ChatRoom create(String issueTitle) {
        return new ChatRoom(UUID.randomUUID().toString(), issueTitle, ChatTrialStage.STAND_BY, new ArrayList<>());
    }

    public JoinType validateIfJoinable(Long memberId) {
        Boolean isRecon = this.members.stream()
                .anyMatch(crm -> crm.getMember().getId().equals(memberId));

        if (this.members.size() < 2) {
            return isRecon ? JoinType.RECONNECTION : JoinType.FOR_THE_FIRST_TIME;
        } else {
            if (!isRecon) {
                throw new AppException(ChatExceptions.MAX_CAPACITY_EXCEEDED);
            }

            return JoinType.RECONNECTION;
        }
    }

    public void join(Member member, String nickname) {
        ChatRoomMember chatRoomMember = ChatRoomMember.create(this, member, nickname);
        this.members.add(chatRoomMember);
    }

    public void leave(Long memberId) {
        this.members.removeIf(m -> m.getId().equals(memberId));
    }

}
