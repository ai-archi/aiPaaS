package com.aixone.event.config;

import com.aixone.event.listener.EventListenerManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件监听器自动配置
 * 基于Spring Kafka提供开箱即用的事件监听能力
 * 支持多种MQ中间件的扩展
 */
@Configuration
@EnableKafka
@EnableConfigurationProperties(EventProperties.class)
@ConditionalOnProperty(prefix = "aixone.event", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EventAutoConfiguration {
    
    /**
     * 事件监听器管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public EventListenerManager eventListenerManager() {
        return new EventListenerManager();
    }
    
    
    /**
     * Kafka消费者工厂
     */
    @Bean
    @ConditionalOnMissingBean
    public ConsumerFactory<String, Object> kafkaConsumerFactory(EventProperties properties) {
        Map<String, Object> configProps = new HashMap<>();
        
        // 基础配置
        configProps.put("bootstrap.servers", properties.getKafka().getBootstrapServers());
        configProps.put("group.id", properties.getKafka().getGroupId());
        configProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        configProps.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
        
        // 消费者配置
        configProps.put("auto.offset.reset", properties.getKafka().getAutoOffsetReset());
        configProps.put("enable.auto.commit", properties.getKafka().isEnableAutoCommit());
        configProps.put("auto.commit.interval.ms", properties.getKafka().getAutoCommitIntervalMs());
        configProps.put("session.timeout.ms", properties.getKafka().getSessionTimeoutMs());
        configProps.put("max.poll.records", properties.getKafka().getMaxPollRecords());
        
        // 安全配置
        if (properties.getKafka().getSecurity() != null) {
            EventProperties.KafkaSecurity security = properties.getKafka().getSecurity();
            if (security.getSaslMechanism() != null) {
                configProps.put("security.protocol", security.getSecurityProtocol());
                configProps.put("sasl.mechanism", security.getSaslMechanism());
                configProps.put("sasl.jaas.config", security.getSaslJaasConfig());
            }
        }
        
        // 信任的包
        configProps.put("spring.json.trusted.packages", "com.aixone.event.dto.*,com.aixone.common.*");
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }
    
    /**
     * Kafka监听器容器工厂
     */
    @Bean("kafkaListenerContainerFactory")
    @ConditionalOnMissingBean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory, 
            EventProperties properties) {
        
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        
        // 并发配置
        factory.setConcurrency(properties.getKafka().getConcurrency());
        
        // 容器属性
        ContainerProperties containerProperties = factory.getContainerProperties();
        containerProperties.setPollTimeout(properties.getKafka().getPollTimeout());
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        
        // 错误处理
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        
        return factory;
    }
}