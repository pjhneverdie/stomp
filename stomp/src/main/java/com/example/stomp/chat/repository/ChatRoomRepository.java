package com.example.stomp.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.stomp.chat.domain.ChatRoom;
import com.redis.om.spring.annotations.Query;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByUuid(String chatRoomUUID);

    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
            "LEFT JOIN FETCH cr.members crms " +
            "LEFT JOIN FETCH crms.member " +
            "WHERE cr.uuid = :uuid")
    Optional<ChatRoom> fetchJoinByUuidWithMembers(@Param("uuid") String uuid);

}
