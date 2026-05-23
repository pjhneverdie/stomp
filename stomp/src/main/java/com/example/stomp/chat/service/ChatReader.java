package com.example.stomp.chat.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.zset.Tuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.example.stomp.chat.document.ChatRoomPreview;
import com.example.stomp.chat.domain.ChatMember;
import com.example.stomp.chat.domain.ChatRoom;
import com.example.stomp.chat.dto.ChatCacheChunk;
import com.example.stomp.chat.dto.ChatCacheChunk.ChatMemberMeta;
import com.example.stomp.chat.dto.ChatCacheChunk.ChatRoomMeta;
import com.example.stomp.chat.dto.SimpleChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatReader {

    private final StringRedisTemplate redisTemplate;

    // private final ChatRoomService chatRoomService;

    // private final ChatMessageService chatMessageService;
    // private final ChatMemberService chatMemberService;
    private final ObjectMapper objectMapper;

    /**
      * @formatter:off
      * 
      * 1. fetch top 20 active entries from member:{memberId}:rooms (ZSET).
      * 
      * 2. fetch the entire member:{memberId}:roomPreviews in a single operation(assuming a heavy user scenario where the total number of rooms is at most ~100). 
      *    since redis is single-threaded, a single bulk fetch is more efficient than executing ~20 separate requests. 
      *    
      * 3. perform filtering and transformation at the application layer.
      * 
      * @formatter:on
      */
    public List<ChatRoomPreview> getChatList(Long memberId, Integer count) {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(
                        "member:" + memberId + ":rooms",
                        0,
                        19);

        List<String> roomIds = tuples.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList();

        List<Object> results = redisTemplate.executePipelined(
                (RedisCallback<Object>) connection -> {
                    StringRedisConnection redis = (StringRedisConnection) connection;

                    for (String roomId : roomIds) {
                        redis.hGetAll("member:" + memberId + ":roomPreview:" + roomId);
                    }

                    return null;
                });

        List<Map<String, String>> previews = results.stream()
                .map(result -> (Map<String, String>) result)
                .toList();
        
    }

    // public List<ChatCacheChunk> loadChatCacheChunk(List<String>
    // unloadedChatRoomUuid) {
    // // 1. Fetch the target chatrooms meta info.
    // List<ChatRoomMeta> chatMetas =
    // chatRoomService.getAllChatRoomMetaByRoomUuid(unloadedChatRoomUuid);

    // if (chatMetas.isEmpty()) {
    // return List.of();
    // }

    // // 2. Fetch the all user's meta info joined in that rooms.
    // List<ChatMemberMeta> chatMemberMetas =
    // chatMemberService.getAllChatMemberMetaByRoomUuid(
    // chatMetas.stream()
    // .map(ChatRoomMeta::roomUuid)
    // .toList());

    // Map<String, List<ChatMemberMeta>> memberMapByRoomUuid =
    // chatMemberMetas.stream()
    // .collect(Collectors.groupingBy(ChatMemberMeta::roomUuid));

    // // 3. Make chunks will be loaded in redis.
    // List<ChatCacheChunk> chatCacheChunks = chatMetas.stream()
    // .map(chatMeta -> {
    // List<ChatMemberMeta> myChatMembersMeta =
    // memberMapByRoomUuid.getOrDefault(chatMeta.roomUuid(),
    // List.of());
    // return new ChatCacheChunk(chatMeta, myChatMembersMeta);
    // })
    // .toList();

    // saveChatCacheChunks(chatCacheChunks);

    // return chatCacheChunks;
    // }

    // public void saveChatCacheChunks(List<ChatCacheChunk> chunks) {
    // if (chunks == null || chunks.isEmpty()) {
    // return;
    // }

    // redisTemplate.executePipelined(new SessionCallback<Object>() {
    // @Override
    // public <K, V> Object execute(RedisOperations<K, V> operations) {
    // StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) operations;

    // for (ChatCacheChunk chunk : chunks) {
    // ChatCacheChunk.ChatRoomMeta roomMeta = chunk.chatMeta();
    // List<ChatCacheChunk.ChatMemberMeta> members = chunk.chatMembers();
    // String roomId = roomMeta.roomUuid();

    // // 1. 방 메타 정보 등록 (Hash)
    // String metaKey = "chat:room:" + roomId + ":meta";
    // stringRedisTemplate.opsForHash().put(metaKey, "uuid", roomMeta.roomUuid());
    // stringRedisTemplate.opsForHash().put(metaKey, "title",
    // roomMeta.issueTitle());
    // stringRedisTemplate.opsForHash().put(metaKey, "trial_stage",
    // roomMeta.chatTrialStage() != null ? roomMeta.chatTrialStage().name() : "");
    // stringRedisTemplate.opsForHash().put(metaKey, "last_message",
    // roomMeta.lastMessage() != null ? roomMeta.lastMessage() : "");
    // stringRedisTemplate.opsForHash().put(metaKey, "last_activated_at",
    // roomMeta.lastActivedAt() != null ?
    // roomMeta.lastActivedAt().format(DATE_FORMATTER) : "");

    // // 2. 방의 최신 시퀀스 등록 (String)
    // String seqKey = "chat:room:" + roomId + ":seq";
    // String totalSeqStr = roomMeta.totalSequence() != null ?
    // String.valueOf(roomMeta.totalSequence())
    // : "0";
    // stringRedisTemplate.opsForValue().set(seqKey, totalSeqStr);

    // // 3. 유저별 읽은 시퀀스 등록 (Hash)
    // if (members != null && !members.isEmpty()) {
    // String readSeqKey = "chat:room:" + roomId + ":read_seq";

    // for (ChatCacheChunk.ChatMemberMeta member : members) {
    // String fieldName = "user_" + member.memberId();
    // String readSeqValue = member.readSequence() != null ?
    // String.valueOf(member.readSequence())
    // : "0";

    // stringRedisTemplate.opsForHash().put(readSeqKey, fieldName, readSeqValue);
    // }
    // }
    // }
    // return null; // 파이프라인 내부 리턴값은 null 고정
    // }
    // });
    // }

    // public List<ChatRoomResponseDto> getMyChatList(Long memberId) {
    // List<ChatRoomResponseDto> dto = new ArrayList<>();

    // int targetSize = 20;

    // List<String> joinedLatestActiveRoomUuids =
    // chatMemberService.getJoinedLastestActiveRoomUuiDsById(memberId,
    // targetSize);

    // if (joinedLatestActiveRoomUuids.isEmpty()) {
    // return List.of();
    // }

    // String userTempKey = "chat:user:" + memberId + ":temp_rooms";
    // String globalSortingKey = "chat:rooms:sorting";
    // String intersectResultKey = "chat:user:" + memberId + ":sorted_rooms";

    // try {
    // // 2. 유저가 참여 중인 방 ID들을 Redis 임시 Set에 보관 (교집합 연산을 위해)
    // redis.opsForSet().add(userTempKey, joinedLatestActiveRoomUuids.toArray(new
    // String[0]));
    // // 혹시 모를 메모리 누수 방지를 위해 임시 키에 TTL 10초 설정
    // redis.expire(userTempKey, 10, TimeUnit.SECONDS);

    // // 3. 글로벌 정렬 ZSET과 유저 참여 방 SET의 교집합(Intersection)을 구해 정렬된 결과를 만듦
    // // 대규모 서비스에선 ZINTERSTORE 연산이 매우 빠름
    // redis.opsForZSet().intersectAndStore(globalSortingKey, userTempKey,
    // intersectResultKey);
    // redis.expire(intersectResultKey, 10, TimeUnit.SECONDS);

    // // 4. 교집합 결과에서 스코어(최신 타임스탬프)가 높은 순(역순)으로 딱 20개만 컷오프! (첫 페이지)
    // // 0부터 19까지 가져오면 상위 20개가 됨
    // Set<String> top20RoomIds =
    // redis.opsForZSet().reverseRange(intersectResultKey, 0, 19);

    // // top20RoomIds들 HASH 조회해서 ChatRoomResponseDto 만듬

    // // 5. ★ [핵심] 만약 레디스 결과가 타겟(20개)보다 적고, 내가 참여한 전체 방은 더 많다면?
    // if (top20RoomIds.size() < targetSize && joinedLatestActiveRoomUuids.size() >
    // top20RoomIds.size()) {

    // // 레디스 결과에 '포함되지 않은' 나머지 내 방 ID들을 추출
    // List<String> missingRooms = joinedLatestActiveRoomUuids.stream()
    // .filter(roomId -> !top20RoomIds.contains(roomId))
    // .toList();

    // List<ChatCacheChunk> chatCacheChunks = loadChatCacheChunk(missingRooms);

    // dto.addAll(refineInMyPointOfView(memberId, chatCacheChunks));
    // }

    // return dto;

    // } finally {
    // redisTemplate.delete(userTempKey);
    // redisTemplate.delete(intersectResultKey);
    // }
    // }

    // private List<ChatRoomResponseDto> refineInMyPointOfView(Long memberId,
    // List<ChatCacheChunk> chatCacheChunks) {
    // // chatCacheChunks는 내가 참여중인 채팅방 정보랑, 내가 참여중인 채팅방에 들어간 모든 유저가 들어있음.
    // // 이것들로 각 채팅방별 기본 정보, 여기서 내가 사용중인 닉네임, total_seq - read_seq해서 안 읽은 게 몇 개 인지
    // 만들어서
    // // 반환

    // }

}