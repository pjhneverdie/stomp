package com.example.stomp.chat.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.stomp.app.domain.BaseEntity;
import com.example.stomp.app.dto.exception.AppException;
import com.example.stomp.chat.dto.ChatExceptions;
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

    @Column(nullable = false)
    private String issueTitle;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatTrialStage trialStage;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMember> members;

    @Column(nullable = false)
    private LocalDateTime lastActivedAt;

    public static ChatRoom create(String issueTitle) {
        return new ChatRoom(UUID.randomUUID().toString(), issueTitle, ChatTrialStage.STAND_BY, new ArrayList<>(),
                LocalDateTime.of(1970, 1, 1, 0, 0));
    }

    private void validateIfJoinable() {
        if (this.members.size() >= 2) {
            throw new AppException(ChatExceptions.MAX_CAPACITY_EXCEEDED);
        }
    }

    public void join(Member member, String nickname) {
        validateIfJoinable();
        this.members.add(ChatMember.create(this, member, nickname));
    }

    public void leave(Long memberId) {
        this.members.removeIf(m -> m.getId().equals(memberId));
    }

}
