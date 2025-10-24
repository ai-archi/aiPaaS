package com.aixone.event.example;

import com.aixone.event.annotation.EventListener;
import com.aixone.event.annotation.EventListeners;
import com.aixone.event.dto.EventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 事件监听器使用示例
 * 展示如何使用@EventListener注解监听事件
 * 基于Spring Kafka的@KafkaListener能力，支持多种MQ中间件
 */
@Component
public class EventListenerExample {
    
    private static final Logger logger = LoggerFactory.getLogger(EventListenerExample.class);
    
    /**
     * 监听用户登录事件
     * 只监听user-events Topic中的user.login事件
     */
    @EventListener(
        topics = "user-events", 
        eventTypes = "user.login",
        groupId = "user-service",
        id = "user-login-listener",
        description = "处理用户登录事件"
    )
    public void handleUserLogin(EventDTO event) {
        logger.info("用户登录事件 - EventId: {}, UserId: {}", 
            event.getEventId(), event.getData());
        
        // 处理用户登录逻辑
        // 例如：更新用户状态、发送欢迎邮件等
    }
    
    /**
     * 监听用户登出事件
     */
    @EventListener(
        topics = "user-events", 
        eventTypes = "user.logout",
        groupId = "user-service",
        id = "user-logout-listener",
        description = "处理用户登出事件"
    )
    public void handleUserLogout(EventDTO event) {
        logger.info("用户登出事件 - EventId: {}, UserId: {}", 
            event.getEventId(), event.getData());
        
        // 处理用户登出逻辑
        // 例如：清理用户会话、记录登出时间等
    }
    
    /**
     * 监听所有订单事件
     * 监听order-events Topic中的所有事件类型
     */
    @EventListener(
        topics = "order-events",
        groupId = "order-service",
        id = "order-events-listener",
        description = "处理所有订单相关事件"
    )
    public void handleOrderEvents(EventDTO event) {
        logger.info("订单事件 - EventId: {}, EventType: {}, Data: {}", 
            event.getEventId(), event.getEventType(), event.getData());
        
        // 根据事件类型进行不同处理
        switch (event.getEventType()) {
            case "order.created":
                handleOrderCreated(event);
                break;
            case "order.paid":
                handleOrderPaid(event);
                break;
            case "order.cancelled":
                handleOrderCancelled(event);
                break;
            default:
                logger.warn("未知的订单事件类型: {}", event.getEventType());
        }
    }
    
    /**
     * 监听多个Topic的事件
     * 同时监听user-events和order-events两个Topic
     */
    @EventListener(
        topics = {"user-events", "order-events"},
        eventTypes = {"user.login", "order.created"},
        groupId = "notification-service",
        id = "notification-listener",
        description = "处理需要发送通知的事件"
    )
    public void handleNotificationEvents(EventDTO event) {
        logger.info("通知事件 - EventId: {}, EventType: {}, Topic: {}", 
            event.getEventId(), event.getEventType(), event.getSource());
        
        // 发送通知逻辑
        // 例如：发送邮件、短信、推送等
    }
    
    /**
     * 高优先级监听器
     * 用于处理关键业务事件
     */
    @EventListener(
        topics = "payment-events",
        eventTypes = "payment.failed",
        groupId = "payment-service",
        id = "payment-failed-listener",
        priority = -1, // 高优先级
        description = "处理支付失败事件（高优先级）"
    )
    public void handlePaymentFailed(EventDTO event) {
        logger.error("支付失败事件 - EventId: {}, Data: {}", 
            event.getEventId(), event.getData());
        
        // 处理支付失败逻辑
        // 例如：发送告警、回滚订单、通知用户等
    }
    
    /**
     * 自定义容器工厂的监听器
     * 展示如何使用自定义的容器工厂
     */
    @EventListener(
        topics = "custom-events",
        groupId = "custom-service",
        id = "custom-events-listener",
        containerFactory = "customKafkaListenerContainerFactory",
        errorHandler = "customErrorHandler",
        description = "使用自定义容器工厂的监听器"
    )
    public void handleCustomEvents(EventDTO event) {
        logger.info("自定义事件 - EventId: {}, EventType: {}", 
            event.getEventId(), event.getEventType());
        
        // 处理自定义事件逻辑
    }
    
    /**
     * 使用Topic模式匹配的监听器
     * 监听所有以"user-"开头的Topic
     */
    @EventListener(
        topicPattern = "user-.*",
        groupId = "user-pattern-service",
        id = "user-pattern-listener",
        description = "监听所有用户相关Topic"
    )
    public void handleUserPatternEvents(EventDTO event) {
        logger.info("用户模式事件 - EventId: {}, EventType: {}", 
            event.getEventId(), event.getEventType());
        
        // 处理用户模式事件逻辑
    }
    
    /**
     * 使用重复注解的监听器
     * 展示如何在同一个方法上使用多个@EventListener注解
     */
    @EventListeners({
        @EventListener(
            topics = "inventory-events",
            groupId = "inventory-service",
            id = "inventory-listener-1",
            eventTypes = "inventory.low"
        ),
        @EventListener(
            topics = "inventory-events",
            groupId = "inventory-service",
            id = "inventory-listener-2",
            eventTypes = "inventory.out"
        )
    })
    public void handleInventoryEvents(EventDTO event) {
        logger.info("库存事件 - EventId: {}, EventType: {}, Data: {}", 
            event.getEventId(), event.getEventType(), event.getData());
        
        // 处理库存逻辑
        // 例如：更新库存、发送补货通知等
    }
    
    // 私有方法
    private void handleOrderCreated(EventDTO event) {
        logger.info("处理订单创建: {}", event.getData());
    }
    
    private void handleOrderPaid(EventDTO event) {
        logger.info("处理订单支付: {}", event.getData());
    }
    
    private void handleOrderCancelled(EventDTO event) {
        logger.info("处理订单取消: {}", event.getData());
    }
}
