package com.example.stomp.application.util;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import com.example.stomp.application.infra.websocket.WsMemberPrincipal;

public final class StompHeaderUtil {

    private StompHeaderUtil() {
    }

    public static WsMemberPrincipal getPrincipal(StompHeaderAccessor accessor) {
        return (WsMemberPrincipal) accessor.getUser();
    }

}
