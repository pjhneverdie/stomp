package com.example.stomp.infrastructure.persistence.jpa.repository;

import com.example.stomp.infrastructure.persistence.jpa.entity.OutboxEvent;

public interface OutboxEventRepository {
    public OutboxEvent save();
}
