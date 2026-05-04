package com.example.stomp.app.infra.websocket.handshake;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.stomp.app.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MtipleWsSessionValidationHSInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        /**
         * @formatter:off
         * 
         * 1. ws disconnected -> the wsSessionId field in redis session will be deleted no matter what the reason is.
         * 2. based on RedisHttpSessionMemberPrincipal will created by redis session, 
         * 'getWsSessionId()' will return null. 
         * 
         * And we are doing handshake only if the 'wsSessionId' is null. 
         * 
         * @formatter:on
         */
        return SecurityUtil.getPrincipal().getWsSessionId() == null; // It says they will be declined except for the
                                                                     // connection for the first time or reconnection.
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {

    }

}
