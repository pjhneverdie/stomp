package com.example.stomp.application.infra.outbox;

import com.example.stomp.application.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox extends BaseEntity {

    private String aggregateType;

    private String aggregateId;

    private String eventType;

    @Column(columnDefinition = "json")
    private String payload;

    public static Outbox of(
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload) {
        Outbox outbox = new Outbox();
        outbox.aggregateType = aggregateType;
        outbox.aggregateId = aggregateId;
        outbox.eventType = eventType;
        outbox.payload = payload;
        return outbox;
    }
}