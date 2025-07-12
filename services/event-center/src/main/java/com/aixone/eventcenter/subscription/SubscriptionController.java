package com.aixone.eventcenter.subscription;

import com.aixone.eventcenter.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 订阅接口
 * /api/subscriptions
 */
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;

    private static final String TENANT_HEADER = "X-Tenant-Id";

    /**
     * 注册新订阅
     */
    @PostMapping
    public ApiResponse<Subscription> registerSubscription(@RequestHeader(value = TENANT_HEADER, required = false) String tenantId, @RequestBody Subscription subscription) {
        if (tenantId == null || tenantId.isEmpty()) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return ApiResponse.success(subscriptionService.registerSubscription(subscription, tenantId));
    }

    /**
     * 查询所有订阅
     */
    @GetMapping
    public ApiResponse<List<Subscription>> getAllSubscriptions(@RequestHeader(value = TENANT_HEADER, required = false) String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return ApiResponse.success(subscriptionService.getAllSubscriptions(tenantId));
    }

    /**
     * 按ID查询订阅
     */
    @GetMapping("/{id}")
    public ApiResponse<Subscription> getSubscriptionById(@RequestHeader(value = TENANT_HEADER, required = false) String tenantId, @PathVariable Long id) {
        if (tenantId == null || tenantId.isEmpty()) {
            return ApiResponse.error(40001, "缺少租户ID");
        }
        return subscriptionService.getSubscriptionById(id, tenantId)
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(40401, "订阅不存在"));
    }
} 