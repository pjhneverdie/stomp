package com.example.stomp.chat.repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatPresenceRepository {
    private final StringRedisTemplate stringRedisTemplate;

    private static final String PARTICIPANTS_KEY_PREFIX = "chat:participants:"; // Set (방 참여자 목록)
    private static final String USER_CONN_PREFIX = "chat:connection:";

    public void enter(String roomId, String userId, String newSocketId) {
        String script = "redis.call('SADD', KEYS[1], ARGV[1]) " + // 1. 참여자 목록(Set)에 추가
                "local old = redis.call('get', KEYS[2]) " + // 2. 기존 소켓 정보 가져오기
                "redis.call('set', KEYS[2], ARGV[2]) " + // 3. 새 소켓 정보로 갱신
                "return old"; // 4. 이전 소켓 ID 반환

        RedisScript<String> redisScript = RedisScript.of(script, String.class);

        // KEYS[1]: 방 참여자 목록 키, KEYS[2]: 유저 접속 정보 키
        // ARGV[1]: 유저 ID, ARGV[2]: 새 소켓 ID
        String oldSocketId = stringRedisTemplate.execute(
                redisScript,
                Arrays.asList(PARTICIPANTS_KEY_PREFIX + roomId, USER_CONN_PREFIX + userId),
                userId, newSocketId);

        // 중복 접속 처리 (기존 세션이 있다면 튕기기 로직 실행)
        if (oldSocketId != null && !oldSocketId.equals(newSocketId)) {
            handleDisconnection(userId, oldSocketId);
        }
    }


    private void handleDisconnection(String userId, String oldSocketId) {
        // 여기에 Pub/Sub으로 튕기기 메시지 발행 로직 구현
        // stringRedisTemplate.convertAndSend("chat:kill", userId + ":" + oldSocketId);
    }

}
