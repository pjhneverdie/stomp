package com.example.stomp.app.infra.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.stomp.app.infra.rabbitmq.config.RabbitMQProperties;
import com.example.stomp.app.infra.websocket.handshake.SecurityContextIntegrationHandShakeHandler;
import com.example.stomp.chat.ws.stomp.interceptor.ChatInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final RabbitMQProperties rabbitMQProperties;
    private final SecurityContextIntegrationHandShakeHandler handShakeHandler;
    private final ChatInterceptor chatInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(handShakeHandler)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");

        registry.enableStompBrokerRelay("/topic")
                .setRelayHost(rabbitMQProperties.host())
                .setRelayPort(rabbitMQProperties.stomp().port())
                .setClientLogin(rabbitMQProperties.username())
                .setClientPasscode(rabbitMQProperties.password())
                .setSystemLogin(rabbitMQProperties.username())
                .setSystemPasscode(rabbitMQProperties.password());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatInterceptor);
    }

}
