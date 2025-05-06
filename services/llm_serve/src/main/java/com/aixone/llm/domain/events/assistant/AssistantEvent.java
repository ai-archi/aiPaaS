package com.aixone.llm.domain.event.assistant;

import com.aixone.llm.domain.event.DomainEvent;

/**
 * 助理事件基类
 */
public abstract class AssistantEvent extends DomainEvent {
    protected AssistantEvent(String eventType, String assistantId, int version) {
        super(eventType, assistantId, version);
    }
} 