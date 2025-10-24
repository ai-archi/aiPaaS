package com.aixone.event.api;

import com.aixone.event.dto.EventDTO;
import com.aixone.common.api.ApiResponse;
import java.time.Instant;
import java.util.List;

/**
 * 事件相关接口协议
 * 基于事件中心的EventController设计，提供事件发布和查询的核心接口
 */
public interface EventApi {
    
    /**
     * 发布事件（仅持久化到数据库）
     * 对应事件中心的 POST /api/events
     */
    ApiResponse<EventDTO> publishEvent(EventDTO eventDTO);

    /**
     * 发布事件到指定Topic
     * 对应事件中心的 POST /api/events/kafka/{topicName}
     */
    ApiResponse<EventDTO> publishEventToTopic(String topicName, EventDTO eventDTO);

    /**
     * 查询所有事件
     * 对应事件中心的 GET /api/events
     */
    ApiResponse<List<EventDTO>> getAllEvents();

    /**
     * 根据ID查询事件
     * 对应事件中心的 GET /api/events/{id}
     */
    ApiResponse<EventDTO> getEventById(Long eventId);

    /**
     * 根据事件类型查询事件
     * 对应事件中心的 GET /api/events/type/{eventType}
     */
    ApiResponse<List<EventDTO>> getEventsByType(String eventType);

    /**
     * 根据时间范围查询事件
     * 对应事件中心的 GET /api/events/time-range
     */
    ApiResponse<List<EventDTO>> getEventsByTimeRange(Instant startTime, Instant endTime);

    /**
     * 根据关联ID查询事件
     * 对应事件中心的 GET /api/events/correlation/{correlationId}
     */
    ApiResponse<List<EventDTO>> getEventsByCorrelationId(String correlationId);

    /**
     * 获取事件统计
     * 对应事件中心的 GET /api/events/stats
     */
    ApiResponse<EventStats> getEventStats();

    /**
     * 事件统计DTO
     */
    class EventStats {
        private long totalCount;

        public EventStats(long totalCount) {
            this.totalCount = totalCount;
        }

        public long getTotalCount() { return totalCount; }
        public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    }
} 