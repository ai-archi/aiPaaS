package com.aixone.eventcenter.schedule.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 任务实体
 * 存储任务元数据和调度策略
 */
@Entity
@Table(name = "schedule_tasks")
@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends com.aixone.common.ddd.Entity<Long> {
    
    /**
     * 任务ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;
    
    /**
     * 任务名称
     */
    @Column(name = "task_name", nullable = false, length = 100)
    private String taskName;
    
    /**
     * 任务描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * 任务类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;
    
    /**
     * 调度策略（Cron表达式或时间间隔）
     */
    @Column(name = "schedule_expression", nullable = false, length = 200)
    private String scheduleExpression;
    
    /**
     * 任务状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status = TaskStatus.PENDING;
    
    /**
     * 执行微服务名称
     */
    @Column(name = "executor_service", nullable = false, length = 100)
    private String executorService;
    
    /**
     * 任务参数（JSON格式）
     */
    @Column(name = "task_params", columnDefinition = "TEXT")
    private String taskParams;
    
    /**
     * 最大重试次数
     */
    @Column(name = "max_retry_count")
    private Integer maxRetryCount = 3;
    
    /**
     * 当前重试次数
     */
    @Column(name = "current_retry_count")
    private Integer currentRetryCount = 0;
    
    /**
     * 上次执行时间
     */
    @Column(name = "last_execute_time")
    private Instant lastExecuteTime;
    
    /**
     * 下次执行时间
     */
    @Column(name = "next_execute_time")
    private Instant nextExecuteTime;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private Instant createTime = Instant.now();
    
    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    private Instant updateTime = Instant.now();
    
    /**
     * 创建者
     */
    @Column(name = "creator", length = 100)
    private String creator;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled = true;
    
    /**
     * 超时时间（秒）
     */
    @Column(name = "timeout_seconds")
    private Integer timeoutSeconds = 300;
    
    /**
     * 租户ID
     */
    @Column(name = "tenant_id", length = 50)
    private String tenantId;
    
    /**
     * 构造函数
     */
    public Task() {
        super(0L); // 临时ID，实际保存时会生成
        this.tenantId = getTenantId(); // 从基类获取租户ID
    }
    
    /**
     * 获取任务ID（重写父类方法）
     */
    @Override
    public Long getId() {
        return taskId;
    }
    
    /**
     * 设置任务ID
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
        this.id = taskId; // 同步到父类的id字段
    }
    
    /**
     * 构造函数
     */
    public Task(String taskName, String description, TaskType taskType, 
                String scheduleExpression, String executorService, String taskParams) {
        this();
        this.taskName = taskName;
        this.description = description;
        this.taskType = taskType;
        this.scheduleExpression = scheduleExpression;
        this.executorService = executorService;
        this.taskParams = taskParams;
    }
    
    /**
     * 构造函数（带租户ID）
     */
    public Task(String taskName, String description, TaskType taskType, 
                String scheduleExpression, String executorService, String taskParams, String tenantId) {
        super(0L, tenantId);
        this.taskName = taskName;
        this.description = description;
        this.taskType = taskType;
        this.scheduleExpression = scheduleExpression;
        this.executorService = executorService;
        this.taskParams = taskParams;
        this.tenantId = tenantId;
    }
    
    /**
     * 更新任务状态
     */
    public void updateStatus(TaskStatus newStatus) {
        this.status = newStatus;
        this.updateTime = Instant.now();
    }
    
    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.currentRetryCount++;
        this.updateTime = Instant.now();
    }
    
    /**
     * 重置重试次数
     */
    public void resetRetryCount() {
        this.currentRetryCount = 0;
        this.updateTime = Instant.now();
    }
    
    /**
     * 更新执行时间
     */
    public void updateExecuteTime(Instant executeTime, Instant nextExecuteTime) {
        this.lastExecuteTime = executeTime;
        this.nextExecuteTime = nextExecuteTime;
        this.updateTime = Instant.now();
    }
    
    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        return this.currentRetryCount < this.maxRetryCount;
    }
    
    /**
     * 暂停任务
     */
    public void pause() {
        this.status = TaskStatus.PAUSED;
        this.enabled = false;
        this.updateTime = Instant.now();
    }
    
    /**
     * 恢复任务
     */
    public void resume() {
        this.status = TaskStatus.PENDING;
        this.enabled = true;
        this.updateTime = Instant.now();
    }
    
    /**
     * 取消任务
     */
    public void cancel() {
        this.status = TaskStatus.CANCELLED;
        this.enabled = false;
        this.updateTime = Instant.now();
    }
}
