package com.aixone.eventcenter.schedule.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import com.aixone.common.ddd.ValueObject;

/**
 * 任务调度策略值对象
 * 封装不同的调度策略
 */
public class ScheduleStrategy extends ValueObject {
    
    /**
     * 调度表达式
     */
    private final String expression;
    
    /**
     * 任务类型
     */
    private final TaskType taskType;
    
    /**
     * 构造函数
     */
    public ScheduleStrategy(String expression, TaskType taskType) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("调度表达式不能为空");
        }
        if (taskType == null) {
            throw new IllegalArgumentException("任务类型不能为空");
        }
        this.expression = expression.trim();
        this.taskType = taskType;
    }
    
    /**
     * 创建Cron调度策略
     */
    public static ScheduleStrategy cron(String cronExpression) {
        return new ScheduleStrategy(cronExpression, TaskType.CRON);
    }
    
    /**
     * 创建一次性任务调度策略
     */
    public static ScheduleStrategy once(Instant executeTime) {
        return new ScheduleStrategy(executeTime.toString(), TaskType.ONCE);
    }
    
    /**
     * 创建间隔任务调度策略
     */
    public static ScheduleStrategy interval(long intervalSeconds) {
        return new ScheduleStrategy(String.valueOf(intervalSeconds), TaskType.INTERVAL);
    }
    
    /**
     * 创建间隔任务调度策略（带单位）
     */
    public static ScheduleStrategy interval(long interval, ChronoUnit unit) {
        long intervalSeconds = unit.getDuration().multipliedBy(interval).getSeconds();
        return new ScheduleStrategy(String.valueOf(intervalSeconds), TaskType.INTERVAL);
    }
    
    /**
     * 检查是否为Cron表达式
     */
    public boolean isCron() {
        return TaskType.CRON.equals(this.taskType);
    }
    
    /**
     * 检查是否为一次性任务
     */
    public boolean isOnce() {
        return TaskType.ONCE.equals(this.taskType);
    }
    
    /**
     * 检查是否为间隔任务
     */
    public boolean isInterval() {
        return TaskType.INTERVAL.equals(this.taskType);
    }
    
    /**
     * 获取间隔秒数（仅对间隔任务有效）
     */
    public long getIntervalSeconds() {
        if (!isInterval()) {
            throw new IllegalStateException("只有间隔任务才能获取间隔秒数");
        }
        try {
            return Long.parseLong(this.expression);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("无效的间隔表达式: " + this.expression);
        }
    }
    
    /**
     * 获取执行时间（仅对一次性任务有效）
     */
    public Instant getExecuteTime() {
        if (!isOnce()) {
            throw new IllegalStateException("只有一次性任务才能获取执行时间");
        }
        try {
            return Instant.parse(this.expression);
        } catch (Exception e) {
            throw new IllegalStateException("无效的时间表达式: " + this.expression);
        }
    }
    
    /**
     * 获取Cron表达式（仅对Cron任务有效）
     */
    public String getCronExpression() {
        if (!isCron()) {
            throw new IllegalStateException("只有Cron任务才能获取Cron表达式");
        }
        return this.expression;
    }
    
    /**
     * 获取调度表达式
     */
    public String getExpression() {
        return expression;
    }
    
    /**
     * 获取任务类型
     */
    public TaskType getTaskType() {
        return taskType;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ScheduleStrategy that = (ScheduleStrategy) obj;
        return Objects.equals(expression, that.expression) && 
               Objects.equals(taskType, that.taskType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(expression, taskType);
    }
    
    @Override
    public String toString() {
        return "ScheduleStrategy{" +
                "expression='" + expression + '\'' +
                ", taskType=" + taskType +
                '}';
    }
}
