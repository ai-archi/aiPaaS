package com.aixone.eventcenter.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订阅实体仓库
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    // 可扩展自定义查询
    java.util.List<Subscription> findByTenantId(String tenantId);
    java.util.Optional<Subscription> findBySubscriptionIdAndTenantId(Long subscriptionId, String tenantId);
} 