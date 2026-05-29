### 대원칙
<p style="line-height: 160%;">
유저한테 보냈다고 하면 그 메시지는 손실나면 안 됨. 손실날 거 같으면 못 보냈다고 해야 함.
<br>
역으로 못 보냈다 했는데 보내지면 안 됨. 요약: <span style="font-weight: bold">보내면 보낸 거고 못 보내면 못 보낸 거야 함.</span>
</p>


### 채팅 발생시,

**1. 채팅 서버 -> kafka에 메시지 produce**
<p style="line-height: 160%;">
메시지 key를 채팅방 uuid로 같은 방에 온 메시지는
<br>
같은 kafka 파티션에 적재되도록 함.
</p>

**1.1. 채팅 서버 -> kafka에 메시지 produce 실패했을 때**
sender 웹소켓에 메시지 전송 실패 메시지 publish.


**2. kafka 컨슈머가 redis 즉시 업데이트, 웹소켓 publish, 기타 캐시 처리, mysql bulk**
<p style="line-height: 160%;">
여기서 포인트가 mysql은 bulk이기 때문에 유저가 서비스 들어왔을 때
<br>
최근 내역을 온전히 못 불러올 수가 있음. 이 방식은 db 부담이 적지만
<br>
실시간 웹소켓이랑 db 싱크가 안 맞다는 거임.
</p>

<p style="line-height: 160%;">
그래서 실시간용 read source에 먼저 메시지를 올리고,
<br>
웹소켓에 publish를 해야 함. read source에 메시지 못
<br>
올리면 메시지 전달 실패 처리해야 함.
</p>

왜냐면 보냈다고 했는데 새로고침 후 못 불러오면 말이 안 됨.

<p style="line-height: 160%;">
즉 read source 반영, 웹소켓 publish는 synchronoss해야 함.
<br>
그래서 read source db가 빠르지 않으면 실시간 채팅 속도에
<br>
영향을 줌. 어차피 redis 쓰고 있으니 추가 인프라 구성 없이 redis 쓰면 됨.
</p>



**2.1. redis 해당 채팅방 시퀀스 관리용 string incr**
<p style="line-height: 160%;">
순서 보장을 위해 seq를 중앙 관리해야 함.
<br>
redis incr은 race condition이어도 안 겹침. 
<br>
문제는 해당 캐시에는 TTL이 있어서 fillback 로직에서 race condition 위험이 있음.
</p>

<p style="line-height: 160%;">
1. a mysql에서 가져옴, 
<br>
2. b mysql에서 가져옴, 
<br>
3. a fillback 완료 후 INCR, 
<br>
4. b fillback 완료 후 INCR -> a랑 b가 같은 seq를 가지고 있음.
</p>

<p style="line-height: 160%;">
흔하진 않지만 이론상 가능하고 상당히 치명적 엣지 케이스임.
<br>
fillback은 아래처럼 해야 함.
</p>

<p style="line-height: 160%;">
1. GET
<br>
2. TTL 때문에 데이터 없으면
<br>
3. SET lock:key uuid NX EX 3 시도
<br>
4. 락 획득 성공한 1명만 DB fillback
<br>
5. cache set
<br>
6. unlock
</p>

조회가 들어가 있는 1, 2, 3은 lua script 씀. 5, 6은 파이프라이닝.

**2.2. 메시지 컨슘해서 redis 해당 방 최근 메시지 zset에 zadd**
read source write 실패 시 sender 웹소켓에 메시지 전송 실패 메시지 publish.
<p style="line-height: 160%;">
zadd 전 incr은 성공했는데 zadd 실패했다고 메시지 전송 실패 처리하면
<br>
1 > 3처럼 seq jump up 현상은 어떡하냐고 생각할 수 있는데
<br>
이건 단순 순서 정렬용이라 1 다음에 3이 와도 크기만 하면 됨.
</p>

<p style="line-height: 160%;">
중요한건 유저한테 실패라고 말해놓고 db에 저장하면 안 됨.
<br>
컨슈머가 kafka commit 처리해서 해당 메시지 log 없애버림.
</p>

**2.3. recipient 웹소켓에 메시지 publish, sender 웹소켓에는 ACK publish**
**2.4. 메시지 일정량 쌓이거나 일정 주기로 mysql bulk insert**

**3. redis 채팅방 목록 불러오기, 최근 메시지 캐싱 기능 구현 상세**

**기능 명세**

1. 채팅방은 항상 최신순으로 정렬해서 20개씩 페이지네이션 가능해야 함.

2. 채팅방 미리보기 카드에는 채팅방 제목, 마지막 메시지, 안 읽은 메시지 카운팅을 제공해야 함.

3. 채팅방 상세 페이지 입장 시 보여줄 50개의 최근 메시지를 캐싱해야 함.


**이용해야 하는 점**
일대일 채팅이라 양 측에 fanout write 부담이 적음.



**주의해야 하는 점**
채팅 도메인 특성상 race condition이 자주 발생함.

**redis 운용 청사진**
**채팅방별 최근 메시지 50개 ZSET, TTL 7일**
Key: chat:{roomUuid}:recent20
Type: ZSET
Score: seq
Member: 메시지 JSON 직렬화 문자열
Value JSON 구조:

{
  "messageType": "A",
  "seq": 152,
  "memberId": 10,
  "senderId": 37,
  "nickname": "abcdef",
  "content": "안녕하세요",
  "createdAt": 1716472000000
}

**채팅방별 시퀀스 관리용 STRING, TTL 7일**
Key: chat:{roomUuid}:totalSeq
Value: totalSeq

**유저별 참여중인 방 ZSET, TTL 없음**
Key: member:{memberId}:rooms
Type: ZSET
Score: lastActivatedAt(ms 단위)
Member: roomUuid

**유저별 채팅방 리스트 미리보기 카드 HASH, TTL 없음**
Key Pattern: member:{memberId}:roomPreview:{roomUuid}
Type: HASH
| Field        | Description     |
|--------------|-----------------|
| uuid         | 채팅방 UUID     |
| title        | 채팅방 제목     |
| lastMessage  | 마지막 메시지   |
| unReadCount  | 안 읽은 개수    |

**채팅방 목록 조회 시**
1. 유저별 참여중인 방 ZSET에서 상위 20개 FETCH

2. 채팅방 리스트 미리보기 카드 HASH 20개 HGETALL



**채팅 발생 시**

**1. 채팅방별 최근 메시지 50개 ZSET에 ZADD, ZREMRANGEBYRANK**
race condition 무상관. ZSET이라 알아서 정렬됨.

**2. 양 측 유저별 참여중인 방 최신 활동순 정렬 ZSET에 현재 시각으로 Score 업데이트**
<p style="line-height: 160%;">
race condition 무상관. ZSET이라 알아서 정렬됨.
<br>
1, 2 번 둘 다 무상관이니 파이프라이닝으로 처리.
</p>

**3. 양 측 유저별 채팅방 리스트 미리보기 카드 HASH에 lastMessage, unreadCount Field Value 업데이트**
<p style="line-height: 160%;">
race condition 상관있음.
<br>
lastMessage는 race condition 발생 시 더 빨리 보낸 메시지가 나중에 온 메시지보다 늦게 redis를 때려서
<br>
정합성이 깨질 수가 있음. 그래서 [seq] + lastMessage로 prefix를 둬서 lastMessage 업데이트 전 조회,
<br>
seq가 낮을 때만 업데이트로 가야 함.
</p>

조회가 하나 추가됐기 때문에 여기도 race condition이 발생할 수 있음.

극단적 예시를 들어보겠음.

<p style="line-height: 160%;">
1. 1 2 3 출발.
<br>
2. 1이 제일 빨라서 최근 메시지가 1로 등록됨.
<br>
3. 다음 2, 3이 1을 동시에 읽고, 나보다 작으니까 동시에 업데이트하려고 함.
<br>
4. 근데 2가 더 느려서 최근 메시지가 3이 아니라 2로 등록됨.
</p>

그렇기 때문에 이 부분은 lua script로 묶어야 찐빠가 안 남.

unreadCount도 마찬가지 유저가 채팅방에 들어와서 0으로 초기화 vs 동시에 채팅 발생해서 1 올리기 race condition이 있음.

어차피 lua script 쓰니까 이것도 같이 처리하면 됨.

**4. 이 구조의 장점**
**1. 채팅 서버, redis, kafka, 컨슈머 중 어떤 게 장애가 나도 대원칙을 준수함.**
<p style="line-height: 160%;">
채팅 서버 or redis or kafka가 꺼지면 채팅 전송이 실패 처리가 되기 때문에
<br>
유저가 보냈는데 안 보내지고, 안 보냈는데 보내지는 찐빠를 방지할 수 있음.
</p>



**2. 채팅 서버, redis, kafka, 컨슈머 중 어떤 게 장애가 나도 복구할 수 있음.**
<p style="line-height: 160%;">
에초에 kafka log 디스크 영속이 채팅 전송 성공 조건이기 때문에 장애 시
<br>
더 이상 채팅은 못 할지언정 데이터가 날아가거나 정합성 깨지는 일 없음.
</p>
