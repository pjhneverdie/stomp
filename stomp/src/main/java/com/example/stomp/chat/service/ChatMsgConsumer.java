package com.example.stomp.chat.service;

import com.example.stomp.chat.controller.ChatMessageController;
import com.example.stomp.chat.repository.ChatMemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.stomp.chat.dto.ChatMessageSendReq;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgConsumer {

    private final ChatMessageController chatMessageController;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatCleanUpService chatCleanUpService;
    private final ChatMsgPublisher chatMsgPublisher;

    ChatMsgConsumer(ChatCleanUpService chatCleanUpService, ChatMemberRepository chatMemberRepository,
            ChatMessageController chatMessageController) {
        this.chatCleanUpService = chatCleanUpService;
        this.chatMemberRepository = chatMemberRepository;
        this.chatMessageController = chatMessageController;
    }

    @KafkaListener(topics = "chat-prepare-topic", groupId = "chat-prep-group", concurrency = "20")
    public void prepareAndValidate(List<ChatMessageSendReq> reqList) {
        // 여기서 먼저 실시간 read source 작업을 다 끝냄.

        // 이제 read source 작업 성공한 애들 vs 실패한 애들 리스트가 완성됨.

        // read source 작업 실패한 애들은 싸그리 한 리스트에 담아서 chat-send-fail-topic에 로그 써서 해당 컨슈머가
        // 웹소켓에 실패 메시지 발송하게 함.

        // 여기서부터는 프론트 ux랑 같이 설계를 해야 함.

        // 프론트에서는 ux 때문에 optimistic response를 써서
        // 처음에는 일단 메시지가 제대로 보내졌다고 처리가 됨. 

        // 나중에 성패 여부를 웹소켓으로 받을 수 있으니까
        // 일단 성공, 결과에 맞춰서 ui를 재렌더링하면 됨.

        // 근데 chat-prepare-topic에 메시지가 진짜 많이 쌓여 있는 경우엔
        // 아직 자기 메시지 처리가 안 된 상태라 새로고침 시
        // read source에 없어서 메시지가 소실될 거임. 

        // 로컬 스토리지 캐싱해서 메시지 유지해야 함.

        // 근데 유저가 메시지 보내고 웹사이트 꺼서 실시간 결과 메시지를 못 받거나
        // 순간의 네트워크 이슈로 chat-send-fail-topic에 로그 쓰기가 실패한다면?

        // read source에 있냐 없냐로만 성패 여부를 확인해야 함.
        // 여기서 엣지 케이스 1개
        // 진짜 메시지 너무 많이 쌓여서 여전히 자기 메시지 처리가 안 됐고
        // read source에 없길래 실패 처리했는데 나중에 처리 결과 성공이면?..
    
        // 즉 read source는 믿을 게 못 됨.

        CompletableFuture<SendResult<String, ChatMessageSendReq>> future = kafkaTemplate.send("chat-send-topic", req);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println(" chat-send-topic 로그 적재 성공! 오프셋: " + result.getRecordMetadata().offset());
            } else {
                // ❌ [카프카 쓰기 실패] 로그 쓰기 실패했으니 바로 웹소켓에 메시지 실패 알림 날림
                System.err.println("❌ chat-send-topic 로그 적재 실패: " + ex.getMessage());
                sendFailNotification(req, "메시지 전송에 실패했습니다. 다시 시도해주세요. (Kafka)");
            }
        });
    }

    // 💡 @KafkaListener가 핵심이야!
    @KafkaListener(topics = "chat-topic", groupId = "chat-consumer-group", concurrency = "3")
    public void consume(List<ChatMessageSendReq> req) {
        // readSource 업데이트
        // 1. redis 해당 채팅방 시퀀스 관리용 string incr
        // 파이프라이닝으로 bulk incr

        List<ChatMessageSendReq> successList = new ArrayList<>();
        List<ChatMessageSendReq> failList = new ArrayList<>();

        // readSource 성공 시 전송
        chatMsgPublisher.pub();

        // 뒤에서 bulk insert

    }
}
