package com.example.stomp.application.infra.websocket;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.example.stomp.application.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SecurityContextIntegrationHandShakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        return WsMemberPrincipal.create(SecurityUtil.getPrincipal());
    }

}
