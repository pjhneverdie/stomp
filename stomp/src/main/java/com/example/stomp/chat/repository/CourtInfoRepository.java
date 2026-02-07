package com.example.stomp.chat.repository;


import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.example.stomp.chat.enum_type.CourtStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CourtInfoRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final HashOperations<String, String, String> hashOps;

    // Redis Key Prefix
    private static final String KEY_PREFIX = "court:info:";

    // Redis Hash Field Names
    public static final String FIELD_NAME = "name";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_REQUIRED_SEATS = "required_seats";

    public void save(String courtId, String name, int requiredSeats) {
        String key = KEY_PREFIX + courtId;

        Map<String, String> courtInfo = new HashMap<>();
        courtInfo.put(FIELD_NAME, name);
        courtInfo.put(FIELD_STATUS, CourtStatus.STAND_BY.toString());
        courtInfo.put(FIELD_REQUIRED_SEATS, String.valueOf(requiredSeats));

        hashOps.putAll(key, courtInfo);
    }

    public void updateField(String courtId, String field, String value) {
        hashOps.put(KEY_PREFIX + courtId, field, value);
    }

    public Map<String, String> get(String courtId) {
        return hashOps.entries(KEY_PREFIX + courtId);
    }

    public Integer getRequiredSeats(String courtId) {
        String count = hashOps.get(KEY_PREFIX + courtId, FIELD_REQUIRED_SEATS);
        return count != null ? Integer.parseInt(count) : 0;
    }

    public void delete(String courtId) {
        stringRedisTemplate.delete(KEY_PREFIX + courtId);
    }

}
