package com.aixone.llm.domain.events;

public enum EventType {
    ASSISTANT_CREATED("ASSISTANT_CREATED", "助理创建"),
    ASSISTANT_UPDATED("ASSISTANT_UPDATED", "助理更新"),
    THREAD_CREATED("THREAD_CREATED", "线程创建"),
    MESSAGE_PROCESSED("MESSAGE_PROCESSED", "消息处理完成"),
    QUOTA_EXCEEDED("QUOTA_EXCEEDED", "配额超限"),
    BILLING_CYCLE_CHANGED("BILLING_CYCLE_CHANGED", "计费周期变更"),
    MODEL_STATUS_CHANGED("MODEL_STATUS_CHANGED", "模型状态变更");

    private final String code;
    private final String name;

    EventType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
} 