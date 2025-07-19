package com.aixone.event.client;

import com.aixone.event.dto.EventDTO;
import com.aixone.event.listener.EventListener;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;

/**
 * Kafka事件客户端，支持事件发布与订阅，Topic注册需统一走事件中心
 */
public class KafkaEventClient {
    private final KafkaProducer<String, String> producer;
    private final KafkaConsumer<String, String> consumer;
    // 本地已注册Topic缓存（实际应定期从事件中心刷新）
    private final Set<String> registeredTopics = new HashSet<>();

    public KafkaEventClient(Properties producerProps, Properties consumerProps) {
        this.producer = new KafkaProducer<>(producerProps);
        this.consumer = new KafkaConsumer<>(consumerProps);
    }

    /** 注册Topic，实际应通过HTTP调用事件中心注册接口 */
    public void registerTopic(String topicName) {
        // TODO: HTTP调用事件中心 /api/topics/register
        throw new UnsupportedOperationException("未实现");
    }

    /** 判断Topic是否已注册（本地缓存，实际应定期刷新） */
    public boolean isTopicRegistered(String topicName) {
        return registeredTopics.contains(topicName);
    }

    /** 发布事件到Kafka，前置校验Topic是否已注册 */
    public void publishEvent(String topic, EventDTO event) {
        if (!isTopicRegistered(topic)) {
            throw new IllegalArgumentException("Topic未注册，请先通过事件中心注册Topic: " + topic);
        }
        // producer.send(new ProducerRecord<>(topic, event.getEventId(), toJson(event)));
        throw new UnsupportedOperationException("未实现");
    }

    /** 订阅Kafka事件，前置校验Topic是否已注册 */
    public void subscribe(String topic, EventListener listener) {
        if (!isTopicRegistered(topic)) {
            throw new IllegalArgumentException("Topic未注册，请先通过事件中心注册Topic: " + topic);
        }
        // consumer.subscribe(Collections.singletonList(topic));
        // while (true) { ... listener.onEvent(eventDTO); }
        throw new UnsupportedOperationException("未实现");
    }

    // 关闭资源等
} 