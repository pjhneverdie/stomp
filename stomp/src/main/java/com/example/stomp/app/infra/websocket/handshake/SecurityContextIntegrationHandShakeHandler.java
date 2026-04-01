package com.example.stomp.app.infra.websocket.handshake;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.example.stomp.chat.dto.SimpleWsPrincipal;

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
public class SecurityContextIntegrationHandShakeHandler extends DefaultHandshakeHandler {

    /**
     * When using WebSocket, you often need HTTP session attributes in the WebSocket
     * session. You might also want STOMP features like '@SendToUser', which rely on
     * Principal.
     *
     * If you are using Spring Security, the Authentication is already stored in the
     * SecurityContext Because handshake are processed with HTTP and we got filters.
     * 
     * In this case, you can simply override 'determineUser()' and wrap the
     * Authentication in a Principal.
     * 
     * This Principal will be associated with the WebSocket session
     * and can later be accessed through StompHeaderAccessor.getUser().
     */

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        return new SimpleWsPrincipal(SecurityContextHolder.getContext().getAuthentication());
    }

}
