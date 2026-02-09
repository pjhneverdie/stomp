package com.example.stomp.chat.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.stomp.chat.enum_type.CourtStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ChatInfoRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final HashOperations<String, String, String> hashOps;

    private static final String KEY_PREFIX = "chat:info:";

    public static final String FIELD_CHAT_NAME = "chat_name";
    public static final String FIELD_COURT_STATUS = "court_status";

    public void create(String courtId, String name, int requiredSeats) {
        String key = KEY_PREFIX + courtId;

        Map<String, String> courtInfo = new HashMap<>();
        courtInfo.put(FIELD_CHAT_NAME, name);
        courtInfo.put(FIELD_COURT_STATUS, CourtStatus.STEP_STAND_BY.toString());

        hashOps.putAll(key, courtInfo);
    }

    public void updateField(String courtId, String field, String value) {
        hashOps.put(KEY_PREFIX + courtId, field, value);
    }

    public Map<String, String> get(String courtId) {
        return hashOps.entries(KEY_PREFIX + courtId);
    }

    public void delete(String courtId) {
        stringRedisTemplate.delete(KEY_PREFIX + courtId);
    }

}

// private final StringRedisTemplate redisTemplate;
//     private final ObjectMapper objectMapper; // JSON 변환기

//     private static final String KEY_PREFIX = "chat:info:";

//     public void save(String courtId, ChatInfo chatInfo) {
//         // 객체를 Map<String, String>으로 자동 변환
//         Map<String, String> map = objectMapper.convertValue(chatInfo, new TypeReference<>() {});
//         redisTemplate.opsForHash().putAll(KEY_PREFIX + courtId, map);
//     }

//     public void updateStatus(String courtId, CourtStatus status) {
//         // 특정 필드만 원자적으로 수정 (JSON 전체를 부를 필요 없음!)
//         redisTemplate.opsForHash().put(KEY_PREFIX + courtId, "courtStatus", status.toString());
//     }

//     public ChatInfo get(String courtId) {
//         // Hash 전체를 가져와서 다시 객체로 변환
//         Map<Object, Object> map = redisTemplate.opsForHash().entries(KEY_PREFIX + courtId);
//         return objectMapper.convertValue(map, ChatInfo.class);
//     }