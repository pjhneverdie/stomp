package com.example.stomp.app.infra.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import com.example.stomp.app.infra.websocket.handshake.SecurityContextIntegrationHandShakeHandler;
import com.example.stomp.app.infra.websocket.service.WebSocketSessionStore;
import com.example.stomp.chat.ws.stomp.interceptor.ChatSubscriptionInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final SecurityContextIntegrationHandShakeHandler handShakeHandler;
    private final ChatSubscriptionInterceptor chatSubscriptionInterceptor;
    private final WebSocketSessionStore inAppSessionStore;

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
        registry.setUserDestinationPrefix("/user");
        registry.enableSimpleBroker("/user");
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatSubscriptionInterceptor);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(handler -> new WebSocketHandlerDecorator(handler) {

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                inAppSessionStore.remove(getMemberIdFromSession(session), session.getId());
                super.afterConnectionClosed(session, closeStatus);
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                inAppSessionStore.add(getMemberIdFromSession(session), session);
                super.afterConnectionEstablished(session);
            }

            private String getMemberIdFromSession(WebSocketSession session) {
                return session.getPrincipal().getName();
            }
        });
    }

}
