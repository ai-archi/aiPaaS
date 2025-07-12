package com.aixone.eventcenter.event;

import com.aixone.eventcenter.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.HttpHeaders;

/**
 * 事件接口
 * /api/events
 */
@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    private static final String TENANT_HEADER = "X-Tenant-Id";

    /**
     * 发布新事件
     */
    @PostMapping
    public ApiResponse<Event> publishEvent(@RequestBody Event event) {
        if (com.aixone.session.SessionContext.getTenantId() == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return ApiResponse.success(eventService.publishEvent(event));
    }

    /**
     * 查询所有事件
     */
    @GetMapping
    public ApiResponse<List<Event>> getAllEvents() {
        if (com.aixone.session.SessionContext.getTenantId() == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return ApiResponse.success(eventService.getAllEvents());
    }

    /**
     * 按ID查询事件
     */
    @GetMapping("/{id}")
    public ApiResponse<Event> getEventById(@PathVariable Long id) {
        if (com.aixone.session.SessionContext.getTenantId() == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return eventService.getEventById(id)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "事件不存在"));
    }
} 