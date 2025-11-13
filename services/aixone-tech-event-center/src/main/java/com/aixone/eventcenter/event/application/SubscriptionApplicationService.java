package com.aixone.eventcenter.event.application;

import com.aixone.common.exception.BizException;
import com.aixone.common.util.ValidationUtils;
import com.aixone.eventcenter.event.domain.Subscription;
import com.aixone.eventcenter.event.domain.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 订阅应用服务
 * 协调领域对象完成订阅业务用例
 */
@Service
@Transactional
public class SubscriptionApplicationService {
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    /**
     * 创建订阅
     */
    public Subscription createSubscription(String tenantId, String eventType, 
                                          String subscriberService, String subscriberEndpoint,
                                          String filterConfig, String retryConfig) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        ValidationUtils.notBlank(eventType, "事件类型不能为空");
        ValidationUtils.notBlank(subscriberService, "订阅服务不能为空");
        ValidationUtils.notBlank(subscriberEndpoint, "订阅端点不能为空");
        
        // 检查是否已存在相同订阅
        List<Subscription> existing = subscriptionRepository.findByTenantIdAndEventType(tenantId, eventType);
        if (existing.stream().anyMatch(s -> s.getSubscriberService().equals(subscriberService) 
                && s.getStatus() == Subscription.SubscriptionStatus.ACTIVE)) {
            throw new BizException("SUBSCRIPTION_ALREADY_EXISTS", 
                "该租户已存在相同事件类型和服务的活跃订阅");
        }
        
        Subscription subscription = new Subscription(tenantId, eventType, subscriberService, subscriberEndpoint);
        if (filterConfig != null) {
            subscription.setFilterConfig(filterConfig);
        }
        if (retryConfig != null) {
            subscription.setRetryConfig(retryConfig);
        }
        
        return subscriptionRepository.save(subscription);
    }
    
    /**
     * 更新订阅
     */
    public Subscription updateSubscription(Long subscriptionId, String tenantId,
                                          String subscriberEndpoint, String filterConfig, String retryConfig) {
        ValidationUtils.notNull(subscriptionId, "订阅ID不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        
        Subscription subscription = subscriptionRepository.findBySubscriptionIdAndTenantId(subscriptionId, tenantId)
                .orElseThrow(() -> new BizException("SUBSCRIPTION_NOT_FOUND", "订阅不存在"));
        
        if (subscriberEndpoint != null) {
            subscription.updateEndpoint(subscriberEndpoint);
        }
        if (filterConfig != null || retryConfig != null) {
            subscription.updateConfig(filterConfig, retryConfig);
        }
        
        return subscriptionRepository.save(subscription);
    }
    
    /**
     * 激活订阅
     */
    public Subscription activateSubscription(Long subscriptionId, String tenantId) {
        ValidationUtils.notNull(subscriptionId, "订阅ID不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        
        Subscription subscription = subscriptionRepository.findBySubscriptionIdAndTenantId(subscriptionId, tenantId)
                .orElseThrow(() -> new BizException("SUBSCRIPTION_NOT_FOUND", "订阅不存在"));
        
        subscription.activate();
        return subscriptionRepository.save(subscription);
    }
    
    /**
     * 停用订阅
     */
    public Subscription deactivateSubscription(Long subscriptionId, String tenantId) {
        ValidationUtils.notNull(subscriptionId, "订阅ID不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        
        Subscription subscription = subscriptionRepository.findBySubscriptionIdAndTenantId(subscriptionId, tenantId)
                .orElseThrow(() -> new BizException("SUBSCRIPTION_NOT_FOUND", "订阅不存在"));
        
        subscription.deactivate();
        return subscriptionRepository.save(subscription);
    }
    
    /**
     * 取消订阅
     */
    public void cancelSubscription(Long subscriptionId, String tenantId) {
        ValidationUtils.notNull(subscriptionId, "订阅ID不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        
        Subscription subscription = subscriptionRepository.findBySubscriptionIdAndTenantId(subscriptionId, tenantId)
                .orElseThrow(() -> new BizException("SUBSCRIPTION_NOT_FOUND", "订阅不存在"));
        
        subscription.cancel();
        subscriptionRepository.save(subscription);
    }
    
    /**
     * 查询租户的所有订阅
     */
    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByTenant(String tenantId) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return subscriptionRepository.findByTenantId(tenantId);
    }
    
    /**
     * 根据ID查询订阅
     */
    @Transactional(readOnly = true)
    public Optional<Subscription> getSubscriptionById(Long subscriptionId, String tenantId) {
        ValidationUtils.notNull(subscriptionId, "订阅ID不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return subscriptionRepository.findBySubscriptionIdAndTenantId(subscriptionId, tenantId);
    }
    
    /**
     * 根据事件类型查询订阅
     */
    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByEventType(String eventType, String tenantId) {
        ValidationUtils.notBlank(eventType, "事件类型不能为空");
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return subscriptionRepository.findByTenantIdAndEventType(tenantId, eventType);
    }
    
    /**
     * 获取事件类型的活跃订阅（用于事件路由）
     */
    @Transactional(readOnly = true)
    public List<Subscription> getActiveSubscriptionsByEventType(String eventType) {
        ValidationUtils.notBlank(eventType, "事件类型不能为空");
        return subscriptionRepository.findByEventTypeAndStatus(eventType, Subscription.SubscriptionStatus.ACTIVE);
    }
    
    /**
     * 统计租户订阅数量
     */
    @Transactional(readOnly = true)
    public long getSubscriptionCountByTenant(String tenantId) {
        ValidationUtils.notBlank(tenantId, "租户ID不能为空");
        return subscriptionRepository.countByTenantId(tenantId);
    }
}

