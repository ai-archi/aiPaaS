package com.aixone.event.api;

import com.aixone.event.dto.SubscriptionDTO;
import com.aixone.common.api.ApiResponse;
import java.util.List;

/**
 * 事件订阅相关接口协议
 */
public interface SubscriptionApi {
    /** 注册事件订阅 */
    ApiResponse<String> registerSubscription(SubscriptionDTO subscriptionDTO);

    /** 查询订阅列表 */
    ApiResponse<List<SubscriptionDTO>> listSubscriptions(String eventType, String tenantId);

    /** 删除订阅 */
    ApiResponse<Boolean> deleteSubscription(String subscriptionId);
} 