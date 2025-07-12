package com.aixone.eventcenter.schedule;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

/**
 * 任务执行日志实体
 * log_id, task_id, status, result, start_time, end_time
 */
@Entity
@Table(name = "job_logs")
@Data
public class JobLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;      // 日志ID
    private Long taskId;     // 关联任务ID
    private String status;   // 执行状态
    @Lob
    private String result;   // 执行结果（JSON）
    private Instant startTime; // 开始时间
    private Instant endTime;   // 结束时间
    private String tenantId; // 租户ID
} 