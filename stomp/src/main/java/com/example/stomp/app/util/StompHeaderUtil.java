package com.example.stomp.app.util;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.Assert;

import com.example.stomp.app.infra.websocket.WsPrincipal;

public final class StompHeaderUtil {

    private StompHeaderUtil() {
    }



    public static WsPrincipal getPrincipal(StompHeaderAccessor accessor) {
        return (WsPrincipal) accessor.getUser();
    }

}
