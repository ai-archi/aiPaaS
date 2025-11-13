package com.aixone.eventcenter.event.domain;

import com.aixone.common.ddd.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 订阅仓储接口
 * 定义订阅聚合的持久化操作
 */
public interface SubscriptionRepository extends Repository<Subscription, Long> {
    
    /**
     * 根据租户ID查找订阅
     */
    List<Subscription> findByTenantId(String tenantId);
    
    /**
     * 根据订阅ID和租户ID查找订阅
     */
    Optional<Subscription> findBySubscriptionIdAndTenantId(Long subscriptionId, String tenantId);
    
    /**
     * 根据事件类型查找订阅
     */
    List<Subscription> findByEventType(String eventType);
    
    /**
     * 根据租户ID和事件类型查找订阅
     */
    List<Subscription> findByTenantIdAndEventType(String tenantId, String eventType);
    
    /**
     * 根据租户ID和状态查找订阅
     */
    List<Subscription> findByTenantIdAndStatus(String tenantId, Subscription.SubscriptionStatus status);
    
    /**
     * 根据事件类型和状态查找活跃订阅
     */
    List<Subscription> findByEventTypeAndStatus(String eventType, Subscription.SubscriptionStatus status);
    
    /**
     * 统计租户的订阅数量
     */
    long countByTenantId(String tenantId);
}

