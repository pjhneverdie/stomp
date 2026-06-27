package com.example.stomp.feature.chat.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.example.stomp.feature.trial.application.constant.RedisKeys;
import com.example.stomp.feature.trial.document.RoomPreview;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatReader {

    // private final StringRedisTemplate redisTemplate;

    // /**
    //   * @formatter:off
    //   * 
    //   * 1. fetch top 20 active entries.
    //   * 
    //   * 2. fetch the preview HASH of each entry.
    //   *    
    //   * 3. parse the HASH FIELD map to dto and return.
    //   * 
    //   * @formatter:on
    // */
    // public List<RoomPreview> getChatRoomList(Long memberId, Pageable pageable) {
    // long start = pageable.getOffset();
    // long end = start + pageable.getPageSize() - 1;

    // // 1.
    // Set<ZSetOperations.TypedTuple<String>> zset = redisTemplate.opsForZSet()
    // .reverseRangeWithScores(
    // RedisKeys.memberRooms(memberId),
    // start,
    // end);

    // if (zset == null || zset.isEmpty()) {
    // // Check if there are no any chat user joined for real or just vacancy of
    // cache.
    // return List.of();
    // }

    // // top 20 active entries.
    // List<String> roomUuids = zset.stream()
    // .map(ZSetOperations.TypedTuple::getValue)
    // .toList();

    // // ZSET to MAP.
    // Map<String, Double> mapZSET = zset.stream()
    // .collect(Collectors.toMap(
    // ZSetOperations.TypedTuple::getValue,
    // ZSetOperations.TypedTuple::getScore));

    // // 2.
    // List<Object> results = redisTemplate.executePipelined(
    // (RedisCallback<Object>) connection -> {
    // StringRedisConnection redis = (StringRedisConnection) connection;

    // for (String roomUuid : roomUuids) {
    // redis.hGetAll(RedisKeys.roomPreview(memberId, roomUuid));
    // }

    // return null;
    // });

    // // HASH MAPs of each entry.
    // List<Map<String, String>> previewMaps = results.stream().map(result ->
    // (Map<String, String>) result).toList();

    // // 3.
    // putFromScoreToLastMessagedAt(previewMaps, mapZSET);
    // return previewMaps.stream().map((previewMap) -> {
    // return RoomPreview.from(previewMap);
    // }).toList();
    // }

    // private void putFromScoreToLastMessagedAt(List<Map<String, String>>
    // previewMaps, Map<String, Double> mapZSET) {
    // previewMaps.forEach(map -> {
    // Double score = mapZSET.get(map.get(RedisKeys.ROOM_PREVIEW_HFKEY_UUID));

    // if (score != null) {
    // map.put(
    // RoomPreview.PREVIEW_DTO_MAP_LAST_MESSAGED_KEY,
    // String.valueOf(score.longValue()));
    // }
    // });
    // }

}