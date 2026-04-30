package com.example.stomp.app.util;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.Assert;

import com.example.stomp.app.infra.websocket.WsMemberPrincipal;

public final class StompHeaderUtil {

    private StompHeaderUtil() {
    }

    public static WsMemberPrincipal getPrincipal(StompHeaderAccessor accessor) {
        return (WsMemberPrincipal) accessor.getUser();
    }

    public static String getNickname(StompHeaderAccessor accessor) {
        return (String) accessor.getHeader("nickname");
    }

}
