package com.aixone.llm.domain.event.quota;

import com.aixone.llm.domain.event.DomainEvent;

/**
 * 配额事件基类
 */
public abstract class QuotaEvent extends DomainEvent {
    protected QuotaEvent(String eventType, String userId, int version) {
        super(eventType, userId, version);
    }
} 