package com.example.stomp.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.stomp.chat.domain.ChatRoomMember;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

        @Query("""
                        select crm
                        from ChatRoomMember crm
                        join fetch crm.chatRoom cr
                        join fetch crm.member m
                        where m.id = :memberId
                              """)
        List<ChatRoomMember> fetchJoinByMemberIdWithChatRoomNMember(@Param("memberId") Long memberId);

}