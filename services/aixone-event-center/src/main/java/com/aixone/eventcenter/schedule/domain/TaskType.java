package com.aixone.eventcenter.schedule.domain;

/**
 * 任务类型枚举
 * 定义不同类型的调度任务
 */
public enum TaskType {
    /**
     * 定时任务（Cron表达式）
     */
    CRON("定时任务"),
    
    /**
     * 一次性任务（按时间点触发）
     */
    ONCE("一次性任务"),
    
    /**
     * 间隔任务（固定间隔执行）
     */
    INTERVAL("间隔任务");

    private final String description;

    TaskType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
