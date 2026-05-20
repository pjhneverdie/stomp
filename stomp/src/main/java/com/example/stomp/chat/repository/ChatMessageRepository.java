package com.example.stomp.chat.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.stomp.chat.domain.ChatMessage;
import com.example.stomp.chat.dto.SimpleChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

        @Query("""
                            select new com.example.chat.dto.ChatMessageView(
                                cm.id,
                                cm.sender.id,
                                cm.content,
                                cm.messageType,
                                cm.createdAt
                            )
                            from ChatMessage cm
                            where cm.chatRoom.uuid = :roomUuid
                              and (:cursor is null or cm.id < :cursor)
                            order by cm.id desc
                        """)
        List<SimpleChatMessage> findMessagesByRoomUuidBeforeCursor(
                        String roomUuid,
                        Long cursor,
                        Pageable pageable);

        @Query("""
                            select new com.example.chat.dto.SimpleChatMessage(
                                cm.id,
                                cm.sender.id,
                                cm.content,
                                cm.messageType,
                                cm.createdAt
                            )
                            from ChatMessage cm
                            where cm.chatRoom.uuid = :roomUuid
                            order by cm.id desc
                        """)
        List<SimpleChatMessage> findRecentMessages(
                        String roomUuid,
                        Pageable pageable);

        @Query("SELECT COUNT(m) FROM ChatMessage m " +
                        "WHERE m.chatRoom.roomUuid = :roomUuid " +
                        "AND m.createdAt > :lastVisitedAt")
        long countUnreadMessages(@Param("roomUuid") String roomUuid,
                        @Param("lastVisitedAt") LocalDateTime lastVisitedAt);

        @Query("SELECT cm.chatRoom.roomUuid, COUNT(m) " +
                        "FROM ChatMember cm " +
                        "JOIN ChatMessage m ON cm.chatRoom.roomUuid = m.chatRoom.roomUuid " +
                        "WHERE cm.member.id = :memberId " +
                        "AND m.createdAt > cm.lastVisitedAt " +
                        "GROUP BY cm.chatRoom.roomUuid")
        List<Object[]> countAllUnreadMessagesByMemberId(@Param("memberId") Long memberId);

        @Query(value = "SELECT m.* FROM (" +
                        "    SELECT *, ROW_NUMBER() OVER (PARTITION BY chat_room_uuid ORDER BY created_at DESC) as rn "
                        +
                        "    FROM chat_message " +
                        "    WHERE chat_room_uuid IN :roomUuuids" +
                        ") m WHERE m.rn <= 20", nativeQuery = true)
        List<ChatMessage> findRecent20MessagesPerRoom(@Param("roomUuuids") List<String> roomUuuids);
}
