package com.example.stomp.app.event;

public record SessionSwitchedEvent(String memberId, String oldSessionId) {
}