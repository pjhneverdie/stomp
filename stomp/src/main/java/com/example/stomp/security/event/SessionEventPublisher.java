package com.example.stomp.security.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.stomp.app.event.SessionSwitchedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "session.switching.exchange";

    @EventListener
    public void handleSessionSwitched(SessionSwitchedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "", event);
    }
}
