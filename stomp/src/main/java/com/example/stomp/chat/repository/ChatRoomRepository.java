package com.example.stomp.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.domain.ChatTrialStage;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

        @Query("""
                        select distinct cr
                        from ChatRoom cr
                        join fetch cr.members m
                        where m.member.id = :memberId
                                """)
        List<ChatRoom> findByMemberIdWithMembers(@Param("memberId") Long memberId);

        @Query("""
                            select count(distinct cr)
                            from ChatRoom cr
                            join cr.members m
                            where m.member.id = :memberId
                              and cr.trialStage <> :stage
                        """)
        long countUnTerminatedTrialByMemberId(
                        @Param("memberId") Long memberId,
                        @Param("stage") ChatTrialStage stage);

        @Query("""
                            select distinct cr
                            from ChatRoom cr
                            left join fetch cr.members
                            where cr.uuid = :uuid
                        """)
        Optional<ChatRoom> findByUuidWithMembers(@Param("uuid") String uuid);

}