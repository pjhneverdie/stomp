package com.example.stomp.application.infra.outbox;

public interface OutboxRepository {
    public Outbox save();
}
