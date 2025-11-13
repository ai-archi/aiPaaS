package com.aixone.eventcenter.schedule.domain;

/**
 * 任务状态枚举
 * 定义任务在执行过程中的各种状态
 */
public enum TaskStatus {
    /**
     * 待执行
     */
    PENDING("待执行"),
    
    /**
     * 执行中
     */
    RUNNING("执行中"),
    
    /**
     * 执行成功
     */
    SUCCESS("执行成功"),
    
    /**
     * 执行失败
     */
    FAILED("执行失败"),
    
    /**
     * 已暂停
     */
    PAUSED("已暂停"),
    
    /**
     * 已取消
     */
    CANCELLED("已取消");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
