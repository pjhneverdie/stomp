package com.example.stomp.chat.repository;

import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatPresenceRepository {
    private final StringRedisTemplate redisTemplate;

    private static final String SESSION_KEY_PREFIX = "chat:participants:";
    private static final String PRESENCE_KEY_PREFIX = "chat:participants:";

    public void saveSessionId(String memberId, String sessionId) {
        redisTemplate.opsForValue().set(SESSION_KEY_PREFIX + memberId, sessionId);

    }

    public Optional<String> getSessionId(String memberId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(SESSION_KEY_PREFIX + memberId));
    }

    public void join(String roomId, String memberId, String sessionId) {
        redisTemplate.opsForSet().add(PRESENCE_KEY_PREFIX + roomId, memberId);
    }

    // 1. 채팅 접속 전, SET에서 get chat:participants:{userID}로 유저가 ID가 존재하는지 확인
    // 2. 없으면 String타입 session:{userID}:{sessionId}
    // 3. 있으면 session:{userID}:{sessionId} 교체
    // 그리고 만약에 다른 기기에서 연결을 하거나 새 창을 켜서 채팅에 또 들어오면 기존 sessionId는 삭제하고 새 sessionId를
    // 등록하는 식으로 재 연결 시켜야 함.

}
