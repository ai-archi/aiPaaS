package com.aixone.eventcenter.event.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.event.application.SubscriptionApplicationService;
import com.aixone.eventcenter.event.domain.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * 订阅管理接口控制器（管理员接口，支持跨租户操作）
 * /api/v1/admin/events/subscriptions
 */
@RestController
@RequestMapping("/api/v1/admin/events/subscriptions")
public class SubscriptionAdminController {
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionAdminController.class);
    
    @Autowired
    private SubscriptionApplicationService subscriptionApplicationService;

    /**
     * 管理员查询订阅列表（可跨租户）
     */
    @GetMapping
    public ApiResponse<List<Subscription>> getSubscriptions(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String eventType) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询订阅列表: tenantId={}, eventType={}", tenantId, eventType);
        
        List<Subscription> subscriptions;
        if (StringUtils.hasText(eventType)) {
            subscriptions = subscriptionApplicationService.getSubscriptionsByEventType(eventType, tenantId);
        } else {
            subscriptions = subscriptionApplicationService.getSubscriptionsByTenant(tenantId);
        }
        
        return ApiResponse.success(subscriptions);
    }

    /**
     * 管理员查询订阅详情（可跨租户）
     */
    @GetMapping("/{subscriptionId}")
    public ApiResponse<Subscription> getSubscriptionById(
            @PathVariable Long subscriptionId,
            @RequestParam(required = false) String tenantId) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ApiResponse.error(40001, "tenantId参数不能为空");
        }
        
        logger.info("管理员查询订阅详情: subscriptionId={}, tenantId={}", subscriptionId, tenantId);
        
        return subscriptionApplicationService.getSubscriptionById(subscriptionId, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "订阅不存在"));
    }
}

