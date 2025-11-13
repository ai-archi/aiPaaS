package com.aixone.eventcenter.event.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.event.application.SubscriptionApplicationService;
import com.aixone.eventcenter.event.domain.Subscription;
import com.aixone.common.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 订阅接口控制器
 * /api/v1/events/subscriptions
 */
@RestController
@RequestMapping("/api/v1/events/subscriptions")
public class SubscriptionController {
    
    @Autowired
    private SubscriptionApplicationService subscriptionApplicationService;

    /**
     * 创建订阅
     */
    @PostMapping
    public ApiResponse<Subscription> createSubscription(@RequestBody CreateSubscriptionRequest request) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        Subscription subscription = subscriptionApplicationService.createSubscription(
                tenantId,
                request.getEventType(),
                request.getSubscriberService(),
                request.getSubscriberEndpoint(),
                request.getFilterConfig(),
                request.getRetryConfig()
        );
        
        return ApiResponse.success(subscription);
    }

    /**
     * 获取当前租户的订阅列表
     */
    @GetMapping
    public ApiResponse<List<Subscription>> getSubscriptions(
            @RequestParam(required = false) String eventType) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        List<Subscription> subscriptions;
        if (eventType != null && !eventType.isEmpty()) {
            subscriptions = subscriptionApplicationService.getSubscriptionsByEventType(eventType, tenantId);
        } else {
            subscriptions = subscriptionApplicationService.getSubscriptionsByTenant(tenantId);
        }
        
        return ApiResponse.success(subscriptions);
    }

    /**
     * 获取订阅详情
     */
    @GetMapping("/{subscriptionId}")
    public ApiResponse<Subscription> getSubscriptionById(@PathVariable Long subscriptionId) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        return subscriptionApplicationService.getSubscriptionById(subscriptionId, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "订阅不存在"));
    }

    /**
     * 更新订阅
     */
    @PutMapping("/{subscriptionId}")
    public ApiResponse<Subscription> updateSubscription(
            @PathVariable Long subscriptionId,
            @RequestBody UpdateSubscriptionRequest request) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        Subscription subscription = subscriptionApplicationService.updateSubscription(
                subscriptionId,
                tenantId,
                request.getSubscriberEndpoint(),
                request.getFilterConfig(),
                request.getRetryConfig()
        );
        
        return ApiResponse.success(subscription);
    }

    /**
     * 激活订阅
     */
    @PutMapping("/{subscriptionId}/activate")
    public ApiResponse<Subscription> activateSubscription(@PathVariable Long subscriptionId) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        Subscription subscription = subscriptionApplicationService.activateSubscription(subscriptionId, tenantId);
        return ApiResponse.success(subscription);
    }

    /**
     * 停用订阅
     */
    @PutMapping("/{subscriptionId}/deactivate")
    public ApiResponse<Subscription> deactivateSubscription(@PathVariable Long subscriptionId) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        Subscription subscription = subscriptionApplicationService.deactivateSubscription(subscriptionId, tenantId);
        return ApiResponse.success(subscription);
    }

    /**
     * 取消订阅
     */
    @DeleteMapping("/{subscriptionId}")
    public ApiResponse<Void> cancelSubscription(@PathVariable Long subscriptionId) {
        String tenantId = SessionContext.getTenantId();
        if (tenantId == null) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        
        subscriptionApplicationService.cancelSubscription(subscriptionId, tenantId);
        return ApiResponse.success(null);
    }

    /**
     * 创建订阅请求DTO
     */
    public static class CreateSubscriptionRequest {
        private String eventType;
        private String subscriberService;
        private String subscriberEndpoint;
        private String filterConfig;
        private String retryConfig;

        // Getters and Setters
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getSubscriberService() { return subscriberService; }
        public void setSubscriberService(String subscriberService) { this.subscriberService = subscriberService; }
        public String getSubscriberEndpoint() { return subscriberEndpoint; }
        public void setSubscriberEndpoint(String subscriberEndpoint) { this.subscriberEndpoint = subscriberEndpoint; }
        public String getFilterConfig() { return filterConfig; }
        public void setFilterConfig(String filterConfig) { this.filterConfig = filterConfig; }
        public String getRetryConfig() { return retryConfig; }
        public void setRetryConfig(String retryConfig) { this.retryConfig = retryConfig; }
    }

    /**
     * 更新订阅请求DTO
     */
    public static class UpdateSubscriptionRequest {
        private String subscriberEndpoint;
        private String filterConfig;
        private String retryConfig;

        // Getters and Setters
        public String getSubscriberEndpoint() { return subscriberEndpoint; }
        public void setSubscriberEndpoint(String subscriberEndpoint) { this.subscriberEndpoint = subscriberEndpoint; }
        public String getFilterConfig() { return filterConfig; }
        public void setFilterConfig(String filterConfig) { this.filterConfig = filterConfig; }
        public String getRetryConfig() { return retryConfig; }
        public void setRetryConfig(String retryConfig) { this.retryConfig = retryConfig; }
    }
}

