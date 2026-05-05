package com.example.stomp.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.stomp.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.participatedRooms " +
            "WHERE m.email = :email")
    Optional<Member> fetchJoinByEmailWithParticipatedRooms(@Param("email") String email);

}
