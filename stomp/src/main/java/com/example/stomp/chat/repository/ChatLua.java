package com.example.stomp.chat.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class ChatLua {

    @Bean
    public RedisScript<Long> previewCardUpdateScript() {

        String script = """
                -- KEYS[1] = sender's zset key
                -- KEYS[2] = recipient's zset key
                -- KEYS[3] = sender's hash key
                -- KEYS[4] = recipient's hash key

                -- ARGV[1] = score
                -- ARGV[2] = roomUuid
                -- ARGV[3] = lastMessage
                -- ARGV[4] = lastMessageSeq

                redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2])
                redis.call('ZADD', KEYS[2], ARGV[1], ARGV[2])

                local message = ARGV[3]
                local seq = tonumber(ARGV[4])

                -- sender
                local senderSeq = redis.call('HGET', KEYS[3], 'lastMessageSeq')

                if (not senderSeq) or (seq > tonumber(senderSeq)) then
                    redis.call('HSET', KEYS[3],
                        'lastMessageSeq', seq,
                        'lastMessage', message
                    )
                end

                -- receiver
                local receiverSeq = redis.call('HGET', KEYS[4], 'lastMessageSeq')

                if (not receiverSeq) or (seq > tonumber(receiverSeq)) then
                    redis.call('HSET', KEYS[4],
                        'lastMessageSeq', seq,
                        'lastMessage', message
                    )
                end

                redis.call('HINCRBY', KEYS[4], 'unReadCount', 1)

                return 1
                                """;

        return RedisScript.of(script, Long.class);
    }
}