package com.aixone.eventcenter.event.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.event.application.EventApplicationService;
import com.aixone.eventcenter.event.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 事件管理接口控制器（管理员接口，支持跨租户操作）
 * /api/v1/admin/events
 */
@RestController
@RequestMapping("/api/v1/admin/events")
public class EventAdminController {
    private static final Logger logger = LoggerFactory.getLogger(EventAdminController.class);
    
    @Autowired
    private EventApplicationService eventApplicationService;

    /**
     * 管理员查询事件列表（可跨租户）
     */
    @GetMapping
    public ApiResponse<List<Event>> getEvents(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询事件列表: tenantId={}, eventType={}, startTime={}, endTime={}", 
                tenantId, eventType, startTime, endTime);
        
        List<Event> events;
        if (StringUtils.hasText(eventType)) {
            events = eventApplicationService.getEventsByType(eventType, tenantId);
        } else if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
            Instant start = Instant.parse(startTime);
            Instant end = Instant.parse(endTime);
            events = eventApplicationService.getEventsByTimeRange(tenantId, start, end);
        } else {
            events = eventApplicationService.getEventsByTenant(tenantId);
        }
        
        return ApiResponse.success(events);
    }

    /**
     * 管理员查询事件详情（可跨租户）
     */
    @GetMapping("/{eventId}")
    public ApiResponse<Event> getEventById(
            @PathVariable Long eventId,
            @RequestParam(required = false) String tenantId) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询事件详情: eventId={}, tenantId={}", eventId, tenantId);
        
        return eventApplicationService.getEventById(eventId, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "事件不存在"));
    }

    /**
     * 管理员删除事件
     */
    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> deleteEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) String tenantId) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员删除事件: eventId={}, tenantId={}", eventId, tenantId);
        
        // TODO: 实现删除逻辑
        return ApiResponse.success(null);
    }
}

