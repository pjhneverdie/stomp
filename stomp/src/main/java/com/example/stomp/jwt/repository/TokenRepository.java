package com.example.stomp.jwt.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.stomp.jwt.config.JwtContants;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public void saveRefreshToken(long id, String token, long validity) {
        String key = JwtContants.REFRESH_TOKEN_PREFIX + token;

        stringRedisTemplate.opsForValue().set(
                key,
                String.valueOf(id),
                Duration.ofMillis(validity));
    }

    public void blackAccessToken(String token, String reason, long leftValidity) {
        String key = JwtContants.BLACKLIST_PREFIX + token;

        stringRedisTemplate.opsForValue().set(
                key,
                reason,
                Duration.ofMillis(leftValidity));
    }

    public void deleteRefreshToken(String token) {
        String key = JwtContants.REFRESH_TOKEN_PREFIX + token;

        stringRedisTemplate.delete(key);
    }

    public boolean isBlackedAccessToken(String token) {
        String key = JwtContants.BLACKLIST_PREFIX + token;

        return stringRedisTemplate.hasKey(key);
    }

    public boolean doesExistRefreshToken(String token) {
        String key = JwtContants.REFRESH_TOKEN_PREFIX + token;

        return stringRedisTemplate.hasKey(key) && stringRedisTemplate.opsForValue().get(key).equals(token);
    }

}
