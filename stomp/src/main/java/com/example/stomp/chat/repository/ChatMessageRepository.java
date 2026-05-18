package com.example.stomp.chat.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.stomp.chat.domain.ChatMessage;
import com.example.stomp.chat.dto.SimpleChatMessage;
import com.redis.om.spring.annotations.Query;

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

    @Query("""
            select count(cm)
            from ChatMessage cm
            where cm.chatRoom.uuid = :roomUuid
            and cm.id > :lastReadMessageId
            """)
    long countUnread(String roomUuid, Long lastReadMessageId);
}
