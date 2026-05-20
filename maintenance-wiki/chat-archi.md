# Welcome to Remix + Vite!


최신 채팅방 정렬용 ZSET

ZSET(chat:rooms:sorting): Score = lastest_activity, Value = room_id

안 읽은 메시지 카운팅, Redison
```
// Key -> chat:room:{room_id}:meta
{
  "uuid": 42,
  "seq": 1521,
  "last_message": "지금 출발해!",
  "trial_stage": "STAND_BY",
  "members": {
    "user_101": { "nickname": "제미나이", "read_seq": 1521, "trial_stage": "STAND_BY" },
    "user_202": { "nickname": "개발왕", "read_seq": 1500, "trial_stage": "STAND_BY" }
  }
}
```

메시지 최적화
```
LPUSH chat:room:{room_id}:messages "{\"msg_id\": 123, \"sender\": 101, \"text\": \"안녕\"}"
LTRIM chat:room:{room_id}:messages 0 99
```
레디스를 쓰기 저장소로 쓰기 때문에 chat_message 테이블 스키마에 msg_id 필드를 하나 까고 

메시지 보낼 때 JSON.NUMINCRBY 
```JSON.NUMINCRBY chat:room:42:meta $.total_seq 1``` 쳐서 msg_id를 확보해야 함.

그 다음 rabbitMQ에 메시지 쏴버리는거야.
그럼 비동기 워커가 그거 가져가서 계속 저장쳐버려. 포인트는 깔작이지말고 벌크로 쳐야해 모았다가.


방 메타 정보 (상대적으로 자주 안 바뀜)

Key: chat:room:{room_id}:meta (Hash 또는 RedisJson)

Field: uuid, title, trial_stage 등

방의 최신 시퀀스 (메시지 보낼 때만 상승)

Key: chat:room:{room_id}:seq (String) -> 메시지 올 때마다 INCR 명령어로 원자적 증가

유저별 읽은 시퀀스 (각 유저가 읽을 때만 상승)

Key: chat:room:{room_id}:read_seq (Hash)

Field: user_101 -> 1521, user_202 -> 1500