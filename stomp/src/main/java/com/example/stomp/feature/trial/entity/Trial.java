package com.example.stomp.feature.trial.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.example.stomp.entity.BaseEntity;
import com.example.stomp.feature.member.domain.Member;
import com.example.stomp.feature.trial.application.trial.dto.ChatExceptions;
import com.example.stomp.shared.exception.AppException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Trial {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(length = 16)
    private UUID id;

    @Column(nullable = false)
    private String issueTitle;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrialStage trialStage;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "trial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrialMember> members;

    public static Trial create(Member member, String issueTitle, String nickname) {
        Trial cr = new Trial(
                UUID.randomUUID(),
                issueTitle,
                TrialStage.STAND_BY,
                 null, 
                 null,
                new ArrayList<>());

        TrialMember cm = TrialMember.create(cr, member, nickname);
        cr.members.add(cm);

        return cr;
    }

    public void join(Member member, String nickname) {
        if (this.members.size() >= 2) {
            throw new AppException(ChatExceptions.MAX_CAPACITY_EXCEEDED);
        }

        this.members.add(TrialMember.create(this, member, nickname));
    }

    public TrialMember getChatRoomMemberById(Long memberId) {
        return this.members.stream()
                .filter((chatRoomMember) -> chatRoomMember.getId() == memberId).findFirst()
                .orElseThrow(() -> {
                    throw new AppException(ChatExceptions.UNEXISTS_CHAT_ROOM_MEMBER);
                });
    }

    public void leave(Long memberId) {
        this.members.removeIf(m -> m.getId().equals(memberId));
    }

}
