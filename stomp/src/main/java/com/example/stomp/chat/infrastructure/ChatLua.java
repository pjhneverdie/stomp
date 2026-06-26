package com.example.stomp.chat.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class ChatLua {

    @Bean
    public RedisScript<Long> updatePersonelViewAndRecentMessage() {

        String script = """
                -- ===============================================================
                -- KEYS[1] : chat:{roomUuid}:recent50
                -- KEYS[2] : member:{senderMId}:rooms
                -- KEYS[3] : member:{senderMId}:room_preview:{roomUuid}
                -- KEYS[4] : member:{recipientMId}:rooms ->
                -- KEYS[5] : member:{recipientMId}:room_preview:{roomUuid}

                -- ARGV[1] : score
                -- ARGV[2] : roomUuid
                -- ARGV[3] : lastMessage
                -- ARGV[4] : lastMessageSeq
                -- ARGV[5] : msgJson
                -- ARGV[6] : ttlSeconds
                -- ===============================================================

                

                local seq = tonumber(ARGV[4])
                local message = ARGV[3]

                ---------------------------------------------------------------
                -- 1. add the message into zset and remain the most recent 50 messages only.
                ---------------------------------------------------------------
                redis.call('ZADD', KEYS[1], seq, ARGV[5])
                redis.call('EXPIRE', KEYS[1], tonumber(ARGV[6]))
                redis.call('ZREMRANGEBYRANK', KEYS[1], 0, -51)

                ---------------------------------------------------------------
                -- 2. update chat list feature related cache.
                ---------------------------------------------------------------
                redis.call('ZADD', KEYS[2], ARGV[1], ARGV[2])

                local senderSeq = redis.call('HGET', KEYS[3], 'lastMessageSeq')
                if (not senderSeq) or (seq > tonumber(senderSeq)) then
                    redis.call('HSET', KEYS[3],
                        'lastMessageSeq', seq,
                        'lastMessage', message
                    )
                end

                ---------------------------------------------------------------
                -- 3. update chat list feature related cache if there is a recipient.
                ---------------------------------------------------------------
                if KEYS[4] and KEYS[5] then
                    redis.call('ZADD', KEYS[4], ARGV[1], ARGV[2])

                    local receiverSeq = redis.call('HGET', KEYS[5], 'lastMessageSeq')
                    if (not receiverSeq) or (seq > tonumber(receiverSeq)) then
                        redis.call('HSET', KEYS[5],
                            'lastMessageSeq', seq,
                            'lastMessage', message
                        )
                    end

                    redis.call('HINCRBY', KEYS[5], 'unReadCount', 1)
                end

                return 1
                                                                        """;

        return RedisScript.of(script, Long.class);
    }
}