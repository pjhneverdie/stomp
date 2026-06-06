package com.example.stomp.app.infra.websocket.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebSocketSessionStore {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userSessions = new ConcurrentHashMap<>();

    public void add(String memberId, WebSocketSession session) {
        sessions.put(session.getId(), session);
        userSessions.computeIfAbsent(memberId, k -> ConcurrentHashMap.newKeySet())
                .add(session.getId());
    }

    public WebSocketSession get(String sessionId) {
        return sessions.get(sessionId);
    }

    public void remove(String memberId, String sessionId) {
        sessions.remove(sessionId);
        Set<String> set = userSessions.get(memberId);
        if (set != null) {
            set.remove(sessionId);
            if (set.isEmpty()) {
                userSessions.remove(memberId);
            }
        }
    }

    public Set<String> getUserSessionIds(String memberId) {
        return userSessions.getOrDefault(memberId, Set.of());
    }
}