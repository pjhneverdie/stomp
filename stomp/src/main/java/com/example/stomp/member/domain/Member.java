package com.example.stomp.member.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.stomp.app.domain.BaseEntity;
import com.example.stomp.chat.domain.ChatRoomMember;
import com.example.stomp.member.domain.enum_type.MemberRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Getter
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = MemberRole.FREE;

    @OneToOne(mappedBy = "member")
    private Credential credential;

    @OneToMany(mappedBy = "member")
    private List<ChatRoomMember> participatedRooms = new ArrayList<>();

    public static Member createMember(String email, String picture) {
        Member member = new Member();
        member.email = email;
        member.picture = picture;
        member.credential = Credential.create(member);

        return member;
    }

    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.toString()));
    }

    public void updateCredential(Credential credential) {
        this.credential = credential;
    }

    // Reflect possible updates on their google or something account every when they
    // sign up.
    public void login(String email, String picture) {
        this.email = email;
        this.picture = picture;
    }

    public void participate(ChatRoomMember chatRoomMember) {
    }

}
