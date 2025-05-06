package com.aixone.llm.domain.event.model;

import com.aixone.llm.domain.event.DomainEvent;

/**
 * 模型事件基类
 */
public abstract class ModelEvent extends DomainEvent {
    protected ModelEvent(String eventType, String modelId, int version) {
        super(eventType, modelId, version);
    }
} 