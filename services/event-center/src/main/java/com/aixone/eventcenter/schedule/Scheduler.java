package com.aixone.eventcenter.schedule;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

/**
 * 调度节点实体
 * scheduler_id, node, status, last_heartbeat
 */
@Entity
@Table(name = "schedulers")
@Data
public class Scheduler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schedulerId;   // 节点ID
    private String node;        // 节点标识
    private String status;      // 节点状态
    private Instant lastHeartbeat; // 最后心跳时间
    private String tenantId; // 租户ID
} 