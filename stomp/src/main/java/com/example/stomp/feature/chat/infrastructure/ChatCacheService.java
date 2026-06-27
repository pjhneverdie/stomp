package com.example.stomp.feature.chat.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.stomp.feature.trial.application.constant.RedisKeys;
import com.example.stomp.feature.trial.application.trial.dto.ChatMessageSendReq;
import com.example.stomp.feature.trial.application.trial.dto.SimpleChatMessage;
import com.example.stomp.feature.trial.application.trial.dto.ChatMessageSendReq.ChatMsgInfo;
import com.example.stomp.feature.trial.application.trial.dto.ChatMessageSendReq.RecipientInfo;
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

        public void cacheJoinedMemberInfo(String memberId, String roomUuid, String chatRoomMemberId,
                        String nickname) {
                // type: set
                // key: chat_room:{roomUuid}:members
                // member: {chatRoomMemberId}
                redisTemplate.opsForSet()
                                .add(RedisKeys.roomMembers(
                                                roomUuid),
                                                chatRoomMemberId);
                // type: hash
                // key: chat_room:{roomUuid}:member:{chatRoomMemberId}
                // field: {memberId}, {nickname}
                redisTemplate.opsForHash().putAll(
                                RedisKeys.roomMember(roomUuid, chatRoomMemberId),
                                Map.of(
                                                RedisKeys.ROOM_MEMBER_HFKEY_MEMBER_ID, memberId,
                                                RedisKeys.ROOM_MEMBER_HFKEY_NICKNAME, nickname));
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

                                                String msgJson = objectMapper.writeValueAsString(msg);

                                                operations.execute(
                                                                chatLua.updatePersonelViewAndRecentMessage(),
                                                                (List<K>) keys,
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
