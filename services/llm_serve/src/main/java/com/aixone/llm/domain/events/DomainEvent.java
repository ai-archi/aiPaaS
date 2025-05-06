package com.aixone.llm.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * 领域事件基类
 */
public abstract class DomainEvent {
    private final String eventId;
    private final String eventType;
    private final Instant occurredOn;
    private final String aggregateId;
    private final int version;

    protected DomainEvent(String eventType, String aggregateId, int version) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.occurredOn = Instant.now();
        this.aggregateId = aggregateId;
        this.version = version;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public int getVersion() {
        return version;
    }
} 