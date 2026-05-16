package com.example.stomp.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.stomp.chat.domain.ChatMember;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    @Query("select crm.chatRoom.uuid from ChatRoomMember crm where crm.member.id = :memberId")
    List<String> findChatRoomUuidsByMemberId(@Param("memberId") Long memberId);

}