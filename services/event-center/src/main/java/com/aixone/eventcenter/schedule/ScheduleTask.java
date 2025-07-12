package com.aixone.eventcenter.schedule;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

/**
 * 调度任务实体
 * task_id, name, cron, type, status, payload, created_at
 */
@Entity
@Table(name = "schedule_tasks")
@Data
public class ScheduleTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;    // 任务ID
    private String name;    // 任务名称
    private String cron;    // Cron表达式
    private String type;    // 任务类型
    private String status;  // 任务状态
    @Lob
    private String payload; // 任务负载（JSON）
    private Instant createdAt; // 创建时间
    private String tenantId; // 租户ID
} 