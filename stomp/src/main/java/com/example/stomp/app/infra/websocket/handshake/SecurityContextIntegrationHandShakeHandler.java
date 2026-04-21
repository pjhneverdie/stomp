package com.example.stomp.app.infra.websocket.handshake;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.example.stomp.app.infra.websocket.WsMemberPrincipal;
import com.example.stomp.app.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket Handshake Processing Flow
 *
 * 1. WebSocketHttpRequestHandler
 * └─ Execute Interceptor
 * └─ Execute HandshakeHandler
 * 
 * 2. RedisHandShakeHandler
 * └─ Execute RequestUpgradeStrategy
 *
 * 3. RequestUpgradeStrategy
 * └─ Create session
 *
 */

@Slf4j
@Component
public class SecurityContextIntegrationHandShakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        return WsMemberPrincipal.create(SecurityUtil.getPrincipal());
    }

}
