package com.example.stomp.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.dto.ChatCacheChunk;
import com.example.stomp.chat.dto.ChatCacheChunk.ChatRoomMeta;
import com.redis.om.spring.annotations.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoom, Long> {

        Optional<ChatRoom> findByUuid(String chatRoomUUID);

        @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
                        "LEFT JOIN FETCH cr.members crms " +
                        "LEFT JOIN FETCH crms.member " +
                        "WHERE cr.uuid = :uuid")
        Optional<ChatRoom> fetchJoinByUuidWithMembers(@Param("uuid") String uuid);

        @Query("SELECT new com.example.dto.ChatCacheChunk.ChatRoomMeta(" +
                        "  cr.uuid, cr.issueTitle, cr.trialStage, " +
                        "  (SELECT MAX(m.sequence) FROM ChatMessage m WHERE m.chatRoom = cr), " +
                        "  (SELECT cm.content FROM ChatMessage cm WHERE cm.chatRoom = cr ORDER BY cm.sequence DESC FETCH FIRST 1 ROWS ONLY), "
                        +
                        "  cr.lastActivedAt" +
                        ") " +
                        "FROM ChatRoom cr " +
                        "WHERE cr.uuid IN :roomUuids " +
                        "ORDER BY cr.lastActivedAt DESC")
        List<ChatRoomMeta> findAllChatRoomMetasByRoomUuid(@Param("roomUuids") List<String> roomUuids);
}