### The Reason You Have To Go With OUTBOX PATTERN

redis 업데이트 이후 웹소켓 전송 -> mysql 저장 topic에 produce 구조에서

produce가 실패하면 retry해야 함. retry 실패하면 dlq지.
근데 네트워크 장기간 장애로 dlq도 실패하면?

commit 안 하고 뻐기면 네트워크 복구될 때까지 commit을 미뤄야 함.
그럼 문제가 웹소켓 발송도 지연돼서 실시간성에도 영향을 받음
웹소켓 전송을 분리할 수가 없음. 웹소켓 -> read source면 실패했을 때 or 상대방 오프라인일 때 재접속 시 싱크가 안 맞음.

그래서 readsource 반영 후 바로 mysql topic에 produce하지말고
outbox이용해서 

따로 스케쥴러가 fetch해서 insert, 성공 시 outbox에서 제거하는 구조로 가면 됨
이럼 네트워크 장애에도 강함. 
만약 애플리케이션 서버가 죽어도 redis가 가지고 있을 거고
outbox에서 제거 못하고 죽어도 멱등성 db 설계하면 중복 저장해도 됨.
근데 redis가 네트워크 찐빠 이런 건 괜찮은데 스케쥴러가 어차피 계속 시도하니까 
redis 아예 다운되면 쌓인 outbox 메시지들 다 지워짐. 근데 상관 없는 게 
이미 redis에 oujtbox 메시지함에 쓰는 작업을 컨슈머가해서
메시지는 이미 kafka 디스크에 영속이 된 상태임.

mysql에 컬럼을 운용하는거야. 이 메시지가 어디 partition, offset이었는지
그리고 index도 걸어놔.

그다음 redis 아예 꺼지는 장애 발생 시 각 파티션별 저장된 마지막 offset 구해서
거기서부터 다시 redis에 쌓으면 복구까지 완료.