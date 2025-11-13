package com.aixone.eventcenter.config;

import com.aixone.eventcenter.event.domain.EventRepository;
import com.aixone.eventcenter.schedule.domain.TaskRepository;
import com.aixone.eventcenter.schedule.domain.TaskLogRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static org.mockito.Mockito.mock;

/**
 * 测试配置类
 * 提供测试所需的 Mock Bean
 */
@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public EventRepository eventRepository() {
        return mock(EventRepository.class);
    }
    
    @Bean
    @Primary
    public TaskRepository taskRepository() {
        return mock(TaskRepository.class);
    }
    
    @Bean
    @Primary
    public TaskLogRepository taskLogRepository() {
        return mock(TaskLogRepository.class);
    }
    
    @Bean
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
    
    @Bean
    @Primary
    public ProducerFactory<String, Object> producerFactory() {
        return mock(ProducerFactory.class);
    }
}
