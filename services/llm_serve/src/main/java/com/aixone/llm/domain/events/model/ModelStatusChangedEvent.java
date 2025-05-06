package com.aixone.llm.domain.event.model;

/**
 * 模型状态变更事件
 */
public class ModelStatusChangedEvent extends ModelEvent {
    private final ModelStatus oldStatus;
    private final ModelStatus newStatus;

    public ModelStatusChangedEvent(String modelId, int version, 
                                 ModelStatus oldStatus, ModelStatus newStatus) {
        super("MODEL_STATUS_CHANGED", modelId, version);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public ModelStatus getOldStatus() {
        return oldStatus;
    }

    public ModelStatus getNewStatus() {
        return newStatus;
    }
}

/**
 * 模型状态枚举
 */
enum ModelStatus {
    ACTIVE,
    DISABLED,
    DELETED,
    ERROR
} 