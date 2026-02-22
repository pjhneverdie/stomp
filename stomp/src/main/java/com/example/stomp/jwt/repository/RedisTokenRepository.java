package com.example.stomp.jwt.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.stomp.jwt.config.JwtContants;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public void saveRefreshToken(long memberId, String token, long validity) {
        String key = JwtContants.REFRESH_TOKEN_PREFIX + token;

        stringRedisTemplate.opsForValue().set(
                key,
                String.valueOf(memberId),
                Duration.ofMillis(validity));
    }

    public void blackToken(String token, String reason, long validity) {
        String key = JwtContants.BLACKLIST_PREFIX + token;

        stringRedisTemplate.opsForValue().set(
                key,
                reason,
                Duration.ofMillis(validity));
    }

    public void deleteRefreshToken(String token) {
        String key = JwtContants.REFRESH_TOKEN_PREFIX + token;

        stringRedisTemplate.delete(key);
    }

    public boolean isBlackedToken(String token) {
        String key = JwtContants.BLACKLIST_PREFIX + token;

        return stringRedisTemplate.hasKey(key);
    }
}
