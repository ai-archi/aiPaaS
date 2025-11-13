package com.aixone.eventcenter.event.infrastructure;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.EventRouter;
import com.aixone.eventcenter.event.domain.Subscription;
import com.aixone.eventcenter.event.domain.SubscriptionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 事件路由实现
 * 负责根据事件类型和订阅配置进行事件路由决策
 */
@Service
public class EventRouterImpl implements EventRouter {
    private static final Logger logger = LoggerFactory.getLogger(EventRouterImpl.class);
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public List<Subscription> routeSubscriptions(String eventType) {
        // 获取该事件类型的所有活跃订阅
        List<Subscription> subscriptions = subscriptionRepository
                .findByEventTypeAndStatus(eventType, Subscription.SubscriptionStatus.ACTIVE);
        
        logger.debug("事件类型 {} 找到 {} 个活跃订阅", eventType, subscriptions.size());
        return subscriptions;
    }
    
    @Override
    public boolean matchesFilter(Event event, Subscription subscription) {
        // 如果没有过滤配置，则匹配所有事件
        if (subscription.getFilterConfig() == null || subscription.getFilterConfig().isEmpty()) {
            return true;
        }
        
        try {
            // 解析过滤配置（JSON格式）
            JsonNode filterConfig = objectMapper.readTree(subscription.getFilterConfig());
            JsonNode eventData = objectMapper.readTree(event.getEventData());
            
            // 简单的字段匹配过滤
            // 支持格式：{"field": "value"} 或 {"field": {"$eq": "value"}}
            return matchesFilterConfig(eventData, filterConfig);
            
        } catch (Exception e) {
            logger.warn("过滤配置解析失败，默认匹配 - SubscriptionId: {}, Error: {}", 
                    subscription.getSubscriptionId(), e.getMessage());
            // 解析失败时，为了安全起见，不匹配
            return false;
        }
    }
    
    /**
     * 匹配过滤配置
     */
    private boolean matchesFilterConfig(JsonNode eventData, JsonNode filterConfig) {
        if (!filterConfig.isObject()) {
            return false;
        }
        
        // 遍历过滤配置的所有字段，所有字段都必须匹配
        var fieldsIterator = filterConfig.fields();
        while (fieldsIterator.hasNext()) {
            var entry = fieldsIterator.next();
            String fieldName = entry.getKey();
            JsonNode filterValue = entry.getValue();
            
            JsonNode eventFieldValue = eventData.get(fieldName);
            if (eventFieldValue == null) {
                return false; // 字段不存在，不匹配
            }
            
            // 如果过滤值是对象，可能是操作符（如 $eq, $ne, $gt 等）
            if (filterValue.isObject()) {
                // 简化实现：只支持 $eq
                if (filterValue.has("$eq")) {
                    String expectedValue = filterValue.get("$eq").asText();
                    if (!eventFieldValue.asText().equals(expectedValue)) {
                        return false; // 值不匹配
                    }
                }
            } else {
                // 直接值匹配
                if (!eventFieldValue.asText().equals(filterValue.asText())) {
                    return false; // 值不匹配
                }
            }
        }
        
        return true;
    }
}

