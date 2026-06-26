package com.example.stomp.trial.infrastucture.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.stomp.trial.domain.Trial;
import com.example.stomp.trial.domain.TrialStage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaTrialRepository extends JpaRepository<Trial, UUID> {

        @Query("""
                        select distinct cr
                        from ChatRoom cr
                        join fetch cr.members m
                        where m.member.id = :memberId
                                """)
        List<Trial> findByMemberIdWithMembers(@Param("memberId") Long memberId);

        @Query("""
                            select count(distinct cr)
                            from Trial tr
                            join tr.members m
                            where m.member.id = :memberId
                              and tr.trialStage <> :stage
                        """)
        long countUnTerminatedTrialByMemberId(
                        @Param("memberId") Long memberId,
                        @Param("stage") TrialStage stage);

        @Query("""
                            select distinct cr
                            from ChatRoom cr
                            left join fetch cr.members
                            where cr.uuid = :uuid
                        """)
        Optional<Trial> findByUuidWithMembers(@Param("uuid") String uuid);

}