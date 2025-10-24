package com.aixone.eventcenter.event.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.event.application.EventApplicationService;
import com.aixone.eventcenter.event.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.aixone.session.SessionContext;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 事件接口控制器
 * /api/events
 */
@RestController
@RequestMapping("/api/events")
public class EventController {
    
    @Autowired
    private EventApplicationService eventApplicationService;

    /**
     * 发布事件（仅持久化到数据库）
     */
    @PostMapping
    public ApiResponse<Event> publishEvent(@RequestBody EventRequest request) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        Event event = eventApplicationService.publishEvent(
                request.getEventType(),
                request.getSource(),
                request.getData(),
                tenantId
        );
        
        return ApiResponse.success(event);
    }

    /**
     * 发布事件到Kafka Topic
     */
    @PostMapping("/kafka/{topicName}")
    public ApiResponse<Event> publishEventToKafka(@PathVariable String topicName, @RequestBody EventRequest request) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        Event event = eventApplicationService.publishEventToKafka(
                topicName,
                request.getEventType(),
                request.getSource(),
                request.getData(),
                tenantId
        );
        
        return ApiResponse.success(event);
    }

    /**
     * 查询所有事件
     */
    @GetMapping
    public ApiResponse<List<Event>> getAllEvents() {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        return ApiResponse.success(eventApplicationService.getEventsByTenant(tenantId));
    }

    /**
     * 按ID查询事件
     */
    @GetMapping("/{id}")
    public ApiResponse<Event> getEventById(@PathVariable Long id) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        return eventApplicationService.getEventById(id, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "事件不存在"));
    }

    /**
     * 根据事件类型查询事件
     */
    @GetMapping("/type/{eventType}")
    public ApiResponse<List<Event>> getEventsByType(@PathVariable String eventType) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        return ApiResponse.success(eventApplicationService.getEventsByType(eventType, tenantId));
    }

    /**
     * 根据时间范围查询事件
     */
    @GetMapping("/time-range")
    public ApiResponse<List<Event>> getEventsByTimeRange(
            @RequestParam Instant startTime,
            @RequestParam Instant endTime) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        return ApiResponse.success(eventApplicationService.getEventsByTimeRange(tenantId, startTime, endTime));
    }

    /**
     * 根据关联ID查询事件
     */
    @GetMapping("/correlation/{correlationId}")
    public ApiResponse<List<Event>> getEventsByCorrelationId(@PathVariable String correlationId) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        return ApiResponse.success(eventApplicationService.getEventsByCorrelationId(correlationId, tenantId));
    }

    /**
     * 获取事件统计
     */
    @GetMapping("/stats")
    public ApiResponse<EventStats> getEventStats() {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        long count = eventApplicationService.getEventCountByTenant(tenantId);
        return ApiResponse.success(new EventStats(count));
    }

    /**
     * 事件请求DTO
     */
    public static class EventRequest {
        private String eventType;
        private String source;
        private String data;
        private String correlationId;

        // Getters and Setters
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        public String getCorrelationId() { return correlationId; }
        public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    }

    /**
     * 事件统计DTO
     */
    public static class EventStats {
        private long totalCount;

        public EventStats(long totalCount) {
            this.totalCount = totalCount;
        }

        public long getTotalCount() { return totalCount; }
        public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    }
}
