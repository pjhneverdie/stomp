package com.example.stomp.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.example.stomp.chat.dto.ChatMessageSendReq;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgConsumer {

    private final ChatCacheService chatCacheService;
    private final ChatMsgPublisher chatMsgPublisher;

    @KafkaListener(topics = "chat-outbox-topic", groupId = "chat-outbox-topic", concurrency = "20")
    public void updatePublishProduce(List<ChatMessageSendReq> reqs,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        try {
            // lua로 캐시 업데이트, db insert dto write
            // 실패하면 채팅 전송 실패 처리
            // 성공하면 상대방한테 쏴줌

            // 스케쥴러가 redis outbox 목록 읽어서 kafka한테 보냄
            // kafka가 mysql에 insert
            // 스케쥴러 <-> redis 네트워크 장애여도 상관 없음. 주기적으로 땡겨오니까.
            // 스케쥴러 <-> kafka 네트워크 장애여도 상관 없음. produce 성공 시에만 outbox에서 지우면 됨.

            List<Object> results = chatCacheService.updatePersonelViewAndRecentMessageBulk(reqs);

            for (int i = 0; i < reqs.size(); i++) {
                ChatMessageSendReq req = reqs.get(i);
                Object result = results.get(i);

                if (result != null && !(result instanceof Exception)) {
                    chatMsgPublisher.pubSuccess();
                } else {
                    chatMsgPublisher.pubFailure(req.getSenderInfo());
                }
            }
        } 

        // ① Redis 타임아웃 및 네트워크 단절 (가장 흔함)
        // 파이프라인으로 20개를 묶어서 executePipelined()를 호출하고 Redis의 응답을 기다리는데, 순간적으로 채팅 서버와
        // Redis 사이의 네트워크 핑이 튀거나 연결이 툭 끊어진 경우입니다.

        // 자바 입장에서는 Redis로부터 결과를 못 받았으니 통째로 RedisConnectionException이나 TimeoutException을
        // 던지게 되고, 전체 catch문으로 빠집니다.

        // ② Redis의 메모리가 가득 찬 경우 (OOM: Out Of Memory)
        // Redis가 감당할 수 없을 정도로 데이터가 쌓여서 메모리가 100%를 찍으면, Redis는 더 이상 쓰기(SET, HSET 등) 명령을
        // 받지 못하고 에러를 뱉습니다.

        // 이때 파이프라인 안의 Lua 스크립트들이 실행되다가 에러를 뿜으며 자바단으로 예외를 던지게 됩니다.

        // ③ Redis Cluster Failover (마스터-슬레이브 전환 시점)
        // Redis 마스터 노드가 갑자기 죽어서 슬레이브 노드가 마스터로 승격되는 찰나의 순간(보통 수 초 이내)이 있습니다.

        // 이 타이밍에 하필 파이프라인 요청이 들어가면 연결할 마스터가 없어서 전체 묶음이 터집니다.
        // outbox에서 redis 다운 시 대응
        // redis가 12:00에 다운돼서 outbox 기록이 다 없어졌음.
        //
        // mysql 까서 message table id가 가장 높은(가장 최근) 저장 메시지를 찾음
        // kafka log 까서 해당 메시지랑 필드 값이 똑같이 생긴 얘를 찾음.
        // 근데 이새기보다 늦게 쓰여진 로그가있따? 얘네가 다 손실된 애들임.
        //

        // 1 2 3 4 5
        // 다 redis에 쓰기 성공해서 commit한 상태
        // 스케쥴러가 1, 2, 3을 처리하다가 redis가 꺼져버린 거임.
        // 그래서 더 이상 처리할 게 없으니 멈춤.
        // 이 상황에서 mysql에 가장 id 높은 애는 3일꺼임
        // kafka 로그까면 3보다 나중에 쓰여진 로그 4, 5가 소실된 걸 알 수 있음
        // redis 복구 후 4부터 다시 저장하면 됨.

    }

}
