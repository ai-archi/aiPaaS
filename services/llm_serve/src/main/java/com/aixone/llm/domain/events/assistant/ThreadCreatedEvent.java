package com.aixone.llm.domain.event.assistant;

/**
 * 线程创建事件
 */
public class ThreadCreatedEvent extends AssistantEvent {
    private final String threadId;

    public ThreadCreatedEvent(String assistantId, int version, String threadId) {
        super("THREAD_CREATED", assistantId, version);
        this.threadId = threadId;
    }

    public String getThreadId() {
        return threadId;
    }
} 