package com.aixone.event.annotation;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件监听器注解
 * 基于Spring Kafka的@KafkaListener，提供统一的事件监听能力
 * 支持多种MQ中间件的封装和扩展
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @EventListener(topics = "user-events")
 * public void handleUserEvents(EventDTO event) {
 *     // 处理用户事件
 * }
 * 
 * @EventListener(topics = "order-events", groupId = "order-service")
 * public void handleOrderEvents(EventDTO event) {
 *     // 处理订单事件
 * }
 * }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(EventListeners.class)
@KafkaListener
public @interface EventListener {
    
    /**
     * 监听器ID
     * 如果不指定，使用默认ID
     */
    String id() default "";
    
    /**
     * 容器工厂Bean名称
     * 如果不指定，使用默认的容器工厂
     */
    String containerFactory() default "";
    
    /**
     * 监听的Topic列表
     * 支持多个Topic
     */
    String[] topics() default {};
    
    /**
     * Topic模式匹配
     * 支持正则表达式匹配Topic名称
     */
    String topicPattern() default "";
    
    /**
     * 指定Topic分区
     * 可以指定具体的Topic和分区
     */
    TopicPartition[] topicPartitions() default {};
    
    /**
     * 容器组
     * 用于分组管理监听器容器
     */
    String containerGroup() default "";
    
    /**
     * 错误处理器Bean名称
     * 如果不指定，使用默认的错误处理器
     */
    String errorHandler() default "";
    
    /**
     * 消费者组ID
     * 如果不指定，使用默认的组ID
     */
    String groupId() default "";
    
    /**
     * ID是否作为组ID
     * 默认为true
     */
    boolean idIsGroup() default true;
    
    /**
     * 客户端ID前缀
     * 用于生成唯一的客户端ID
     */
    String clientIdPrefix() default "";
    
    /**
     * Bean引用名称
     * 默认为"__listener"
     */
    String beanRef() default "__listener";
    
    /**
     * 监听的事件类型列表
     * 支持多个事件类型，用逗号分隔
     * 例如：{"user.login", "user.logout"}
     * 如果为空，则监听指定Topic的所有事件
     */
    String[] eventTypes() default {};
    
    /**
     * 是否启用监听
     * 默认为true，可以通过配置动态控制
     */
    boolean enabled() default true;
    
    /**
     * 监听器优先级
     * 数值越小优先级越高
     * 默认为0
     */
    int priority() default 0;
    
    /**
     * 监听器描述
     * 用于监控和调试
     */
    String description() default "";
}