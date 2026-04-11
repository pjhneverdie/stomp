package com.example.stomp.app.infra.websocket.session;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import com.example.stomp.app.event.SessionSwitchedEvent;

import lombok.RequiredArgsConstructor;

// @Component
@RequiredArgsConstructor
public class SessionTerminateListener {

    private final SimpUserRegistry userRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "#{autoDeleteQueue.name}")
    public void handleSessionTerminate(SessionSwitchedEvent event) {
        SimpUser user = userRegistry.getUser(event.memberId());

        if (user != null) {
            user.getSessions().forEach(session -> {
                /**
                 * 유저가 다른 기기로 로그인 했을 때 서버 측 대응
                 * 1. 기존 세션을 redis에서 지운다 -> 유저가 기존 기기에서 다시 로그인 해서 않는 이상 기존 유저 재연결 불가. 만약 기존 기기에서
                 * 다시 로그인하면 어차피 기존 기기가 새 기기가 되면서 세션 스위칭 이벤트 발생하니까 redis에서 세션 지웠을 때 일단 기존기기에서
                 * 재연결은 막을 수 있음.
                 * 
                 * 여기서 남은 문제는 유저가 기존 기기로 재연결은 불가능하지만 이미 연결된 세션을 끊어야 한다는 것
                 * 
                 * 2. 이건 웹소켓 세션에 에러 프레임 발송해서 기존 연결을 강제로 끊어 버리면 됨
                 */
                String errorMessage = "Multiple login detected. This session is terminated.";

                // 헤더 설정 (중요: ERROR 프레임으로 인식되게 함)
                StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
                accessor.setMessage("Multiple login detected.");
                accessor.setSessionId(session.getId());

                // 특정 유저에게 에러 프레임 전송
                messagingTemplate.convertAndSendToUser(
                        event.memberId(),
                        "/queue/errors",
                        errorMessage,
                        accessor.getMessageHeaders());

            });
        }
    }

}