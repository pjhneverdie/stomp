package com.example.stomp.chat.repository;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CourtPresenceRepository {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Long> enterCourtScript;

    private static final String PARTICIPANTS_KEY_PREFIX = "court:participants:";
    private static final String INFO_KEY_PREFIX = "court:info:";

    public Long tryEnter(String courtId, String userId) {
        List<String> keys = List.of(
                INFO_KEY_PREFIX + courtId,
                PARTICIPANTS_KEY_PREFIX + courtId);

        // ARGV[1]: 유저ID, ARGV[2]: 상태값, ARGV[3]: 바뀔 상태명(READY)
        return stringRedisTemplate.execute(
                enterCourtScript,
                keys,
                userId,
                JUROR_STATUS_ONLINE,
                CourtStatus.READY.toString());
    }

    /**
     * 재판 퇴장 (단순 삭제)
     */
    public void leave(String courtId, String userId) {
        stringRedisTemplate.opsForHash().delete(PARTICIPANTS_KEY_PREFIX + courtId, userId);
    }

    /**
     * 현재 참여 중인 배심원(Juror) 수 조회
     */
    public Long getJurorCount(String courtId) {
        return stringRedisTemplate.opsForHash().size(PARTICIPANTS_KEY_PREFIX + courtId);
    }
}
