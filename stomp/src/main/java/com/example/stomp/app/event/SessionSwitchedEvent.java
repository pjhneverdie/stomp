package com.example.stomp.app.event;

// issues when a user logins another device without having logout the origin
public record SessionSwitchedEvent(String memberId, String oldSessionId) {
}