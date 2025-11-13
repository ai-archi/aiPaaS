package com.aixone.eventcenter.event.application;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.EventDeliveryRecord;
import com.aixone.eventcenter.event.domain.EventRouter;
import com.aixone.eventcenter.event.domain.Subscription;
import com.aixone.eventcenter.event.infrastructure.EventDistributor;
import com.aixone.eventcenter.event.infrastructure.EventDeliveryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 事件路由服务
 * 负责事件的路由和分发
 */
@Service
@Transactional
public class EventRoutingService {
    private static final Logger logger = LoggerFactory.getLogger(EventRoutingService.class);
    
    @Autowired
    private EventRouter eventRouter;
    
    @Autowired
    private EventDistributor eventDistributor;
    
    @Autowired(required = false)
    private EventDeliveryRecordService deliveryRecordService;
    
    /**
     * 路由并分发事件
     * 根据事件类型查找订阅，过滤后分发到订阅者端点
     */
    public void routeAndDistribute(Event event) {
        try {
            // 1. 根据事件类型查找所有活跃订阅
            List<Subscription> subscriptions = eventRouter.routeSubscriptions(event.getEventType());
            
            if (subscriptions.isEmpty()) {
                logger.debug("事件类型 {} 没有活跃订阅，跳过分发 - EventId: {}", 
                        event.getEventType(), event.getEventId());
                return;
            }
            
            // 2. 根据过滤配置过滤订阅
            List<Subscription> matchedSubscriptions = subscriptions.stream()
                    .filter(subscription -> eventRouter.matchesFilter(event, subscription))
                    .collect(Collectors.toList());
            
            if (matchedSubscriptions.isEmpty()) {
                logger.debug("事件 {} 没有匹配的订阅，跳过分发 - EventId: {}", 
                        event.getEventType(), event.getEventId());
                return;
            }
            
            logger.info("事件 {} 找到 {} 个匹配的订阅，开始分发 - EventId: {}", 
                    event.getEventType(), matchedSubscriptions.size(), event.getEventId());
            
            // 3. 创建分发记录并异步分发到所有匹配的订阅者
            List<CompletableFuture<Boolean>> futures = matchedSubscriptions.stream()
                    .map(subscription -> {
                        // 创建分发记录
                        EventDeliveryRecord record = null;
                        if (deliveryRecordService != null) {
                            record = deliveryRecordService.findOrCreateRecord(
                                    event.getEventId(), 
                                    subscription.getSubscriptionId(), 
                                    event.getTenantId(), 
                                    3 // 默认最大重试3次
                            );
                        }
                        
                        final EventDeliveryRecord finalRecord = record;
                        return CompletableFuture.supplyAsync(() -> {
                            return eventDistributor.distribute(event, subscription, finalRecord);
                        });
                    })
                    .collect(Collectors.toList());
            
            // 4. 等待所有分发完成（可选：可以改为异步处理，不等待）
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            // 5. 统计分发结果
            long successCount = futures.stream()
                    .mapToLong(future -> future.join() ? 1 : 0)
                    .sum();
            
            logger.info("事件分发完成 - EventId: {}, 总数: {}, 成功: {}", 
                    event.getEventId(), matchedSubscriptions.size(), successCount);
            
        } catch (Exception e) {
            logger.error("事件路由和分发失败 - EventId: {}, Error: {}", 
                    event.getEventId(), e.getMessage(), e);
            // 不抛出异常，避免影响事件发布流程
        }
    }
    
    /**
     * 异步路由并分发事件（不阻塞）
     */
    public void routeAndDistributeAsync(Event event) {
        CompletableFuture.runAsync(() -> {
            routeAndDistribute(event);
        });
    }
}

