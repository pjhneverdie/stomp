package com.example.stomp.chat.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.mapping.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.stomp.app.constant.RedisKeys;
import com.example.stomp.chat.dto.ChatCacheReq.ChatRoomMemberCacheReq;
import com.example.stomp.chat.dto.ChatMessageSendReq;
import com.example.stomp.chat.dto.ChatMessageSendReq.ChatMsgInfo;
import com.example.stomp.chat.dto.ChatMessageSendReq.RecipientInfo;

import jakarta.security.auth.message.MessageInfo;

import com.example.stomp.chat.dto.SimpleChatMessage;
import com.example.stomp.chat.repository.ChatLua;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatCacheService {

        private final ChatLua chatLua;
        private final ObjectMapper objectMapper;
        private final StringRedisTemplate redisTemplate;

        private static final long TTL_SECONDS = 7 * 24 * 60 * 60;

        public void cacheChatRoomMember(ChatRoomMemberCacheReq req) {
                redisTemplate.opsForSet()
                                .add(RedisKeys.roomMembers(
                                                req.roomUuid()),
                                                req.chatRoomMemberId());

                redisTemplate.opsForHash().putAll(
                                RedisKeys.roomMember(req.roomUuid(), req.chatRoomMemberId()),
                                Map.of(
                                                RedisKeys.ROOM_MEMBER_HFKEY_MEMBER_ID, req.memberId(),
                                                RedisKeys.ROOM_MEMBER_HFKEY_NICKNAME, req.nickname()));
        }

        public Optional<RecipientInfo> findRecipientInfo(ChatMessageSendReq req) {
                return redisTemplate.opsForSet()
                                .members(RedisKeys.roomMembers(req.getMsgInfo().roomUuid()))
                                .stream()
                                .filter(chatRoomMemberId -> !chatRoomMemberId.equals(
                                                String.valueOf(req.getSenderInfo().chatRoomMemberId())))
                                .findFirst()
                                .map(id -> {
                                        return new RecipientInfo(
                                                        Long.valueOf(id),
                                                        Long.valueOf((String) redisTemplate.opsForHash()
                                                                        .get(RedisKeys.roomMember(
                                                                                        req.getMsgInfo().roomUuid(),
                                                                                        id),
                                                                                        RedisKeys.ROOM_MEMBER_HFKEY_MEMBER_ID)));
                                });
        }

        public List<Object> updatePersonelViewAndRecentMessageBulk(List<ChatMessageSendReq> reqs) {
                return redisTemplate.executePipelined(new SessionCallback<Object>() {
                        @Override
                        public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {

                                for (ChatMessageSendReq req : reqs) {
                                        try {
                                                ChatMsgInfo chatMsgInfo = req.getMsgInfo();
                                                String roomUuid = chatMsgInfo.roomUuid();
                                                String senderMId = String.valueOf(req.getSenderInfo().memberId());
                                                String recipientMId = req.getRecipientInfo() == null
                                                                ? null
                                                                : String.valueOf(req.getRecipientInfo().getMemberId());

                                                List<String> keys = new ArrayList<>();
                                                keys.add(RedisKeys.recent50(roomUuid));
                                                keys.add(RedisKeys.memberRooms(senderMId));
                                                keys.add(RedisKeys.memberRoomPreview(senderMId, roomUuid));

                                                if (recipientMId != null) {
                                                        keys.add(RedisKeys.memberRooms(recipientMId));
                                                        keys.add(RedisKeys.memberRoomPreview(recipientMId, roomUuid));
                                                }

                                                SimpleChatMessage msg = new SimpleChatMessage(
                                                                null,
                                                                chatMsgInfo.seq(),
                                                                chatMsgInfo.content(),
                                                                chatMsgInfo.messageType(),
                                                                chatMsgInfo.createdAt());

                                                // 중요: 파티션과 오프셋 정보도 Outbox 관리를 위해 ARGV나 별도 로직에 태워야 합니다.
                                                // 만약 Lua 스크립트 내부에서 Outbox 데이터도 같이 쌓는다면 ARGV에 req.getKafkaPartition(),
                                                // req.getKafkaOffset()을 추가로 던지세요!
                                                String msgJson = objectMapper.writeValueAsString(msg);

                                                // 2. 파이프라인용 operations 객체에 Lua 스크립트 명령을 쌓아둡니다 (실제 전송 X, 대기열에 추가)
                                                operations.execute(
                                                                chatLua.updatePersonelViewAndRecentMessage(),
                                                                (List<K>) keys, // 제네릭 타입 캐스팅
                                                                String.valueOf(chatMsgInfo.createdAt()),
                                                                roomUuid,
                                                                chatMsgInfo.content(),
                                                                String.valueOf(chatMsgInfo.seq()),
                                                                msgJson,
                                                                String.valueOf(TTL_SECONDS));

                                        } catch (JsonProcessingException e) {
                                                throw new RuntimeException("Redis Pipeline JSON 파싱 에러", e);
                                        }
                                }

                                return null; // executePipelined의 규칙: 무조건 null 반환
                        }
                }); // 👈 이 시점에 20개의 Lua 스크립트가 Redis 서버로 딱 1번의 네트워크로 전송 및 실행됨!
        }

}
