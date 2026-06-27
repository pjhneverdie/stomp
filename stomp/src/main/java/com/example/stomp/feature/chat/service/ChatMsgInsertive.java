package com.example.stomp.feature.chat.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.stomp.feature.chat.infrastructure.ChatMessageBulkInsertRepository;
import com.example.stomp.feature.trial.application.constant.RedisKeys;
import com.example.stomp.feature.trial.application.trial.dto.ChatMessageNativeInsertDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgInsertive {

    // private final ObjectMapper objectMapper;
    // private final StringRedisTemplate redisTemplate;
    // private final ChatMessageBulkInsertRepository bulkInsertRepository;

    // @Scheduled(fixedDelay = 5000)
    // public void insertBulk() {
    //     List<String> stringDtos = redisTemplate.opsForList().range(
    //             RedisKeys.chatMessageOutbox(),
    //             0,
    //             99);

    //     if (stringDtos.isEmpty()) {
    //         return;
    //     }

    //     bulkInsertRepository.bulkInsert(stringDtos.stream().map(json -> {
    //         try {
    //             return objectMapper.readValue(
    //                     json,
    //                     ChatMessageNativeInsertDto.class);
    //         } catch (JsonProcessingException e) {
    //             return null;
    //         }
    //     }).filter(Objects::nonNull).toList());

    // }
}
