package com.aixone.llm.domain.events;

import java.time.Instant;
import java.util.Map;

public class DomainEvent {
    private final String eventId;
    private final EventType eventType;
    private final Instant occurredOn;
    private final String tenantId;
    private final int version;
    private final Map<String, Object> payload;

    public DomainEvent(String eventId, EventType eventType, Instant occurredOn, String tenantId, int version, Map<String, Object> payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.occurredOn = occurredOn;
        this.tenantId = tenantId;
        this.version = version;
        this.payload = payload;
    }

    public String getEventId() { return eventId; }
    public EventType getEventType() { return eventType; }
    public Instant getOccurredOn() { return occurredOn; }
    public String getTenantId() { return tenantId; }
    public int getVersion() { return version; }
    public Map<String, Object> getPayload() { return payload; }
} 