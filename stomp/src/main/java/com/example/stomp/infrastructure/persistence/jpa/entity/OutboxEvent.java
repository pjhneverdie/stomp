package com.example.stomp.infrastructure.persistence.jpa.entity;

import com.example.stomp.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent extends BaseEntity {

    private String aggregateType;

    private String aggregateId;

    private String eventType;

    @Column(columnDefinition = "json")
    private String payload;

    public static OutboxEvent of(
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload) {
        OutboxEvent outbox = new OutboxEvent();
        outbox.aggregateType = aggregateType;
        outbox.aggregateId = aggregateId;
        outbox.eventType = eventType;
        outbox.payload = payload;
        return outbox;
    }
}