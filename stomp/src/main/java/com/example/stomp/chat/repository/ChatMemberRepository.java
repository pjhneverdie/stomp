package com.example.stomp.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.stomp.chat.domain.ChatMember;
import com.example.stomp.chat.dto.ChatCacheChunk;
import com.example.stomp.chat.dto.ChatCacheChunk.ChatMemberMeta;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

        @Query("select cm from ChatMember cm join fetch cm.chatRoom where cm.member.id = :memberId")
        List<ChatMember> findAllByMemberIdWithChatRoom(@Param("memberId") Long memberId);

        @Query("select cm.chatRoom.uuid from ChatMember cm where cm.member.id = :memberId")
        List<String> findChatRoomUuidsByMemberId(@Param("memberId") Long memberId);

        @Query("SELECT new com.example.dto.ChatInitialView$SimpleChatMeta(" +

                        "  r.uuid, r.issueTitle, r.trialStage, " +

                        "  (SELECT MAX(m.sequence) FROM ChatMessage m WHERE m.chatRoom = r), " +

                        "  (SELECT m.content FROM ChatMessage m WHERE m.chatRoom = r ORDER BY m.sequence DESC LIMIT 1)"
                        +

                        "  r.lastActivedAt" +

                        ") " +

                        "FROM ChatRoom r " +

                        "WHERE r.uuid IN (" +

                        "    SELECT cm.chatRoom.uuid FROM ChatMember cm WHERE cm.member.id = :memberId" +

                        ") " +

                        "ORDER BY r.lastActivedAt DESC")

        List<ChatMemberMeta> findAllChatMemberMetasByRoomUuid();

}