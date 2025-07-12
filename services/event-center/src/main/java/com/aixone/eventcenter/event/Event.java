package com.aixone.eventcenter.event;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

/**
 * 事件实体
 * event_id, event_type, source, data, timestamp
 */
@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId; // 事件ID

    private String eventType; // 事件类型
    private String source;    // 事件来源
    private String tenantId;  // 租户ID

    @Lob
    private String data;      // 事件数据（JSON）

    private Instant timestamp; // 事件发生时间
} 