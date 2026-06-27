package com.example.stomp.feature.chat.infrastructure;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.stomp.feature.trial.application.trial.dto.ChatMessageNativeInsertDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatMessageBulkInsertRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkInsert(List<ChatMessageNativeInsertDto> messages) {
        String sql = """
                    INSERT INTO chat_message (
                        chat_room_uuid,
                        chat_room_member_id,
                        seq,
                        message_type,
                        created_at,
                        updated_at,
                        kafka_partition,
                        kafka_offset
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, messages, messages.size(),
                (ps, msg) -> {
                    ps.setString(1, msg.chatRoomUuid());
                    ps.setObject(2, msg.chatRoomMemberId());
                    ps.setLong(3, msg.seq());
                    ps.setString(4, msg.messageType());
                    ps.setObject(5, msg.createdAt());
                    ps.setObject(6, msg.updatedAt());
                    ps.setInt(7, msg.kafkaPartition());
                    ps.setLong(8, msg.kafkaOffset());
                });
    }

}
