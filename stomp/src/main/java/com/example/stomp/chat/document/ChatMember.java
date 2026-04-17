package com.example.stomp.chat.document;

import com.example.stomp.chat.document.enum_type.NetworkStatus;
import com.example.stomp.chat.document.enum_type.PersonnelChapter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMember {

    private String id;

    private String nickname;

    private PersonnelChapter personnelChapter;

    private NetworkStatus networkStatus;

    public static ChatMember create(String id, String nickname) {
        return new ChatMember(id, nickname, PersonnelChapter.STAND_BY, NetworkStatus.CONNECTED);
    }

}
