# 채팅방 목록 불러오기 기능 구현

1. 채팅방은 항상 최신순으로 정렬해서 20개씩 페이지네이션 가능해야 한다.

2. 채팅방 미리보기 카드에는 채팅방 제목, 마지막 메시지, 마지막 활동 시간, 안 읽은 메시지 카운팅이 있어야 한다.  



# 이용해야 하는 점

일대일 채팅이라 fanout write 부담이 적음.



# redis 운용 청사진

**채팅방별 최근 메시지 20개 ZSET, TTL 없음.**

**Key**: chat:{roomUuid}:recent20
**Type**: ZSET
**Score**: seq
**Member**: 메시지 JSON 직렬화 문자열
**Value** JSON 구조: {
  "messageType": "A",
  "seq": 152,
  "memberId": 10,
  "senderId": 37,
  "nickname": "jinhyuk",
  "content": "안녕하세요",
  "createdAt": 1716472000000
}


**채팅방별 시퀀스 관리용 STRING, TTL 없음.**

**Key**: chat:{roomUuid}:totalSeq
**Value**: totalSeq


**유저별 참여중인 방 ZSET, TTL 없음.**

**Key**: member:{memberId}:rooms
**Type**: ZSET
**Score**: lastActivatedAt(ms 단위)
**Member**: roomUuid


**유저별 채팅방 리스트 미리보기 카드 HASH, TTL 없음.**

**Key**: member:{memberId}:roomPreview:{roomUuid}
**Type**: HASH
**Field**	Description
| Field       | Description |
| ----------- | ----------- |
| uuid        | 채팅방 UUID    |
| title       | 채팅방 제목      |
| lastMessage | 마지막 메시지     |
| totalSeq    | 마지막 메시지 시퀀스 |
| readSeq     | 사용자가 읽은 시퀀스 |

# 채팅 발생 시,

1. **chat:{roomId}:totalSeq INCR**

* 받은 seq로 mysql insert용 dto 만들어서 rabbitMQ 큐에 던짐.(현재 redis 쓰기 총 1번) 

* 채팅 저장 역할 컨슈머 서버는 메시지 계속 쌓아두다가 100개 되면 즉시 mysql에 FLUSH, 주기적 3초마다 FLUSH.

이 FLUSH에는 fill back용 source of truth인 mysql에 chat_room table의 last_actived_at 컬럼 업데이트도 포함됨.

* TTL이 없는 이유는, 

TTL을 거는 순간 메모리에 없으면 mysql을 들려야 하는데, 아다리로 TTL 이후 둘 다 동시에 채팅방에 메시지를 보내버리면 race 컨디션이 발생함.

이 race 컨디션을 막으려면 락 걸고 난리쳐야 하는데 어차피 STRING 하나 따리 TTL 안 거는 게 유지보수적으로 이득임.



2. **상대방 웹소켓에 메시지 발송​**

* 모든 작업이 끝나고 웹소켓에 쏴주면 속도가 너무 느림. 일단 바로 쏨.

3. **채팅방별 최근 메시지 20개 ZSET에 ZADD, ZREMRANGEBYRANK**

* ACK 프레임 받으면 3번부터 실행, 못 받으면 에러 프레임 전송 후, 프론트에서 재전송 옵션 제공. 

  그리고 이미 mysql insert 큐에 쐈기 때문에 이것도 취소 시켜야 함. 어케 취소할지는 연구 중.

* redis 해당 방 최근 메시지 ZSET에 바로 추가.(현재 redis 쓰기 총 2번)

* 메모리 절약 ZSET 사이즈 20개 유지 ZREMRANGEBYRANK(현재 redis 쓰기 총 3번)

* 둘은 파이프라이닝.


4. **양 측 유저별 참여중인 방 ZSET, 유저별 채팅방 리스트 미리보기 카드 HASH 최신화.**

* 양 측 유저별 참여중인 방 ZSET에 현재 시각으로 Score 업데이트.(현재 redis 쓰기 총 5번)

* 양 측 유저별 채팅방 리스트 미리보기 카드 HASH에 lastMessage, totalSeq Field Value 업데이트.(현재 redis 쓰기 총 7번)

* 동시에 메시지 보낼 때 race condition은 해결해야 함.!! 고민중..

INCR은 원자적이라 seq는 문제가 없고 ZSET도 seq 기반이라 괜찮은데, HASH에서 꼬일 수가 있음. 이건 그래서 파이프라이닝 말고 lua로 해야 할 수도 있음.

근데 lua는 redis 전체를 멈추게 해서 동접 많으면 채팅 딜레이 발생 가능함. 그래서 차라리 미세한 차이는 들고 가는 것도 .. 음.. 고민.





# 채팅 상세 페이지에서 조회 시,

1. **유저별 채팅방 리스트 미리보기 카드 HASH 최신화.**

* HASH에 readSeq Field Value 업데이트

근데 매번 readSeq write 하는 건 솔직히 굳이임. 상관 없긴한데 차라리 일단 프론트에서 캐싱 후 방 상세 페이지에서 나갈 때 1번, 들어갈 때 1번이 베스트.

왜냠 readSeq 존재 이유가 안 읽은 개수 카운팅용인데, 일단 웹소켓 연결되면 그 이후에는 프론트에서 실시간 처리가 가능하기 때문에 즉각 업데이트 굳이.



이 업데이트에는 fill back용 source of truth인 mysql에 chat_room_member table의 read_seq 컬럼 업데이트도 포함임.

느낌 자체가 이런 느낌. 웹소켓 연결 -> 웹소켓 연결 이후 온 메시지 프론트에서 상태관리 중.... -> 채팅방 리스트 api 요청(redis 조회 뒤에 찰나에 온 메시지는 프론트에서 가지고 있을 거임) -> 

api 응답 vs 프론트에서 가지고 있는 메시지 sync 맞춰서 채팅방 리스트 반환 -> 이후에는 프론트에서 redis 미리보기 카드(최신 메시지, 안 읽은 개수) 실시간 업데이트.

이 구조에서 입,퇴장 시 api로만으로는 갑자기 유저 네트워크가 끊기면 api를 못 보내니까, 

메시지 읽을 때마다 웹소켓으로 서버에 읽음 프레임 보내면 서버가 웹소켓 세션에 기록해서 tracking.

네트워크 끊기면 서버에서 DISCONNECT 이벤트 감지해서 마지막 readSeq로 업데이트 해버리는 거임.

# 채팅방 목록 조회 시,

1. **유저별 참여중인 방 ZSET에서 상위 20개 FETCH.**

2. **채팅방 리스트 미리보기 카드 HASH 20개 HGETALL.**

3. **애플리케이션에서 totalSeq - readSeq 작업 등 dto 완성.**