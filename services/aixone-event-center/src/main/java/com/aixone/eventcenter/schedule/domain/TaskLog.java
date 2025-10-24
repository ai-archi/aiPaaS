package com.aixone.eventcenter.schedule.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 任务执行记录实体
 * 记录每次任务执行结果
 */
@Entity
@Table(name = "schedule_task_logs")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskLog extends com.aixone.common.ddd.Entity<Long> {
    
    /**
     * 记录ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;
    
    /**
     * 任务ID
     */
    @Column(name = "task_id", nullable = false)
    private Long taskId;
    
    /**
     * 执行节点
     */
    @Column(name = "executor_node", length = 100)
    private String executorNode;
    
    /**
     * 开始时间
     */
    @Column(name = "start_time", nullable = false)
    private Instant startTime;
    
    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private Instant endTime;
    
    /**
     * 执行状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;
    
    /**
     * 执行结果
     */
    @Column(name = "result", columnDefinition = "TEXT")
    private String result;
    
    /**
     * 异常信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    /**
     * 异常堆栈
     */
    @Column(name = "error_stack", columnDefinition = "TEXT")
    private String errorStack;
    
    /**
     * 执行耗时（毫秒）
     */
    @Column(name = "duration_ms")
    private Long durationMs;
    
    /**
     * 重试次数
     */
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private Instant createTime = Instant.now();
    
    /**
     * 构造函数
     */
    public TaskLog() {
        super(0L); // 临时ID，实际保存时会生成
    }
    
    /**
     * 获取日志ID（重写父类方法）
     */
    @Override
    public Long getId() {
        return logId;
    }
    
    /**
     * 设置日志ID
     */
    public void setLogId(Long logId) {
        this.logId = logId;
        this.id = logId; // 同步到父类的id字段
    }
    
    /**
     * 构造函数
     */
    public TaskLog(Long taskId, String executorNode, Instant startTime) {
        this();
        this.taskId = taskId;
        this.executorNode = executorNode;
        this.startTime = startTime;
        this.status = TaskStatus.RUNNING;
    }
    
    /**
     * 完成执行
     */
    public void complete(TaskStatus status, String result, Long durationMs) {
        this.endTime = Instant.now();
        this.status = status;
        this.result = result;
        this.durationMs = durationMs;
    }

    /**
     * 执行失败
     */
    public void fail(String errorMessage, String errorStack, Long durationMs) {
        this.endTime = Instant.now();
        this.status = TaskStatus.FAILED;
        this.errorMessage = errorMessage;
        this.errorStack = errorStack;
        this.durationMs = durationMs;
    }
    
    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    
    /**
     * 检查是否执行成功
     */
    public boolean isSuccess() {
        return TaskStatus.SUCCESS.equals(this.status);
    }
    
    /**
     * 检查是否执行失败
     */
    public boolean isFailed() {
        return TaskStatus.FAILED.equals(this.status);
    }
}
