package com.example.stomp.chat.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.stomp.rabbitmq.config.RabbitMQProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final RabbitMQProperties rabbitMQProperties;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");

        registry.enableStompBrokerRelay("/queue")
                .setRelayHost(rabbitMQProperties.host())
                .setRelayPort(rabbitMQProperties.stomp().port())
                .setClientLogin(rabbitMQProperties.username())
                .setClientPasscode(rabbitMQProperties.password())
                .setSystemLogin(rabbitMQProperties.username())
                .setSystemPasscode(rabbitMQProperties.password());
    }

}
