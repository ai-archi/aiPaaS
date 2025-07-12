package com.aixone.eventcenter.audit;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

/**
 * 审计日志实体
 * log_id, event_id, user_id, event_type, data, timestamp
 */
@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;      // 日志ID
    private Long eventId;    // 关联事件ID
    private String userId;   // 操作用户ID
    private String eventType;// 事件类型
    @Lob
    private String data;    // 审计数据（JSON）
    private Instant timestamp; // 审计时间
    private String tenantId; // 租户ID
} 