package com.aixone.event.mq.rocketmq;

import com.aixone.event.annotation.EventListener;
import com.aixone.event.dto.EventDTO;
import com.aixone.event.listener.EventListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RocketMQ事件监听器处理器
 * 基于RocketMQ Spring Boot Starter，处理RocketMQ消息
 * 
 * 注意：这个类需要添加RocketMQ依赖才能使用
 * <dependency>
 *     <groupId>org.apache.rocketmq</groupId>
 *     <artifactId>rocketmq-spring-boot-starter</artifactId>
 *     <version>2.2.3</version>
 * </dependency>
 */
@Component
public class RocketMQEventListenerProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(RocketMQEventListenerProcessor.class);
    
    @Autowired
    private EventListenerManager eventListenerManager;
    
    /**
     * 处理RocketMQ消息
     * 这个方法会被RocketMQ Spring Boot Starter自动调用
     * 
     * 使用示例：
     * @RocketMQMessageListener(
     *     topic = "user-events",
     *     consumerGroup = "user-service-group",
     *     messageModel = MessageModel.CLUSTERING
     * )
     * public void handleUserEvents(EventDTO event) {
     *     // 处理用户事件
     * }
     */
    public void handleRocketMQMessage(EventDTO event, String topic) {
        try {
            logger.debug("收到RocketMQ消息 - Topic: {}, EventId: {}", topic, event.getEventId());
            
            // 获取该Topic的所有监听器
            List<EventListenerManager.ListenerInfo> listeners = eventListenerManager.getListeners(topic);
            
            if (listeners.isEmpty()) {
                logger.warn("Topic {} 没有注册的监听器", topic);
                return;
            }
            
            // 按优先级排序
            listeners.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
            
            // 调用匹配的监听器
            boolean hasMatch = false;
            for (EventListenerManager.ListenerInfo listener : listeners) {
                if (!listener.isEnabled()) {
                    continue;
                }
                
                // 注意：这里可以添加MQ类型检查逻辑
                // 由于当前ListenerInfo没有mqType字段，暂时跳过此检查
                
                // 检查事件类型匹配
                if (listener.isEventTypeMatch(event.getEventType())) {
                    try {
                        // 调用监听器方法
                        listener.getMethod().invoke(listener.getBean(), event);
                        hasMatch = true;
                        
                        logger.debug("RocketMQ事件监听器执行成功 - Topic: {}, Listener: {}.{}, EventType: {}", 
                            topic, listener.getBean().getClass().getSimpleName(), 
                            listener.getMethod().getName(), event.getEventType());
                            
                    } catch (Exception e) {
                        logger.error("RocketMQ事件监听器执行失败 - Topic: {}, Listener: {}.{}, EventId: {}", 
                            topic, listener.getBean().getClass().getSimpleName(), 
                            listener.getMethod().getName(), event.getEventId(), e);
                    }
                }
            }
            
            if (!hasMatch) {
                logger.debug("Topic {} 没有匹配的RocketMQ监听器 - EventType: {}", topic, event.getEventType());
            }
            
        } catch (Exception e) {
            logger.error("处理RocketMQ消息失败 - Topic: {}, EventId: {}", topic, event.getEventId(), e);
        }
    }
}
