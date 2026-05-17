package com.example.stomp.chat.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.stomp.chat.domain.ChatMessage;
import com.example.stomp.chat.dto.ChatMessageView;
import com.redis.om.spring.annotations.Query;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
                select new com.example.chat.dto.ChatMessageView(
                    cmv.id,
                    cmv.sender.id,
                    cmv.content,
                    cmv.messageType,
                    cmv.createdAt
                )
                from ChatMessage cm
                where cm.chatRoom.uuid = :roomUuid
                  and (:cursor is null or cm.id < :cursor)
                order by cm.id desc
            """)
    List<ChatMessageView> findChatMessageViews(
            String roomUuid,
            Long cursor,
            Pageable pageable);

}
