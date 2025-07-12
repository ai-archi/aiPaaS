package com.aixone.eventcenter.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 订阅服务，负责订阅注册与查询
 */
@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    /**
     * 注册新订阅
     */
    public Subscription registerSubscription(Subscription subscription, String tenantId) {
        subscription.setTenantId(tenantId);
        return subscriptionRepository.save(subscription);
    }

    /**
     * 查询所有订阅
     */
    public List<Subscription> getAllSubscriptions(String tenantId) {
        return subscriptionRepository.findByTenantId(tenantId);
    }

    /**
     * 按ID查询订阅
     */
    public Optional<Subscription> getSubscriptionById(Long id, String tenantId) {
        return subscriptionRepository.findBySubscriptionIdAndTenantId(id, tenantId);
    }
} 