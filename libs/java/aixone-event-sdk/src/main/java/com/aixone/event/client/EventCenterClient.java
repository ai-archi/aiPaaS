package com.aixone.event.client;

import com.aixone.event.dto.EventDTO;
import com.aixone.event.dto.SubscriptionDTO;
import com.aixone.event.dto.AuditLogDTO;
import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import java.util.List;

/**
 * 事件中心HTTP客户端（示例，具体实现可用RestTemplate/Feign等）
 */
public class EventCenterClient {
    /** 发布事件 */
    public ApiResponse<String> publishEvent(EventDTO eventDTO) {
        throw new UnsupportedOperationException("未实现");
    }
    /** 注册订阅 */
    public ApiResponse<String> registerSubscription(SubscriptionDTO subscriptionDTO) {
        throw new UnsupportedOperationException("未实现");
    }
    /** 查询事件 */
    public ApiResponse<PageResult<EventDTO>> listEvents(PageRequest pageRequest, String eventType, String tenantId) {
        throw new UnsupportedOperationException("未实现");
    }
    /** 查询订阅 */
    public ApiResponse<List<SubscriptionDTO>> listSubscriptions(String eventType, String tenantId) {
        throw new UnsupportedOperationException("未实现");
    }
    /** 查询审计日志 */
    public ApiResponse<PageResult<AuditLogDTO>> listAuditLogs(PageRequest pageRequest, String eventType, String tenantId) {
        throw new UnsupportedOperationException("未实现");
    }
} 