package com.aixone.event.client;

import com.aixone.event.dto.EventDTO;
import com.aixone.event.dto.TopicDTO;
import com.aixone.event.api.EventApi;
import com.aixone.event.api.TopicApi;
import com.aixone.common.api.ApiResponse;
import java.time.Instant;
import java.util.List;

/**
 * 事件中心HTTP客户端
 * 基于事件中心的REST接口设计，提供事件和Topic管理的统一客户端
 * 
 * 注意：这是一个协议层接口，具体实现由使用者提供（如RestTemplate、Feign等）
 */
public class EventCenterClient implements EventApi, TopicApi {
    
    private final String baseUrl;
    private final String tenantId;
    
    public EventCenterClient(String baseUrl, String tenantId) {
        this.baseUrl = baseUrl;
        this.tenantId = tenantId;
    }
    
    // ========== EventApi 实现 ==========
    
    @Override
    public ApiResponse<EventDTO> publishEvent(EventDTO eventDTO) {
        // 实现 POST /api/events
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<EventDTO> publishEventToTopic(String topicName, EventDTO eventDTO) {
        // 实现 POST /api/events/kafka/{topicName}
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<List<EventDTO>> getAllEvents() {
        // 实现 GET /api/events
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<EventDTO> getEventById(Long eventId) {
        // 实现 GET /api/events/{id}
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<List<EventDTO>> getEventsByType(String eventType) {
        // 实现 GET /api/events/type/{eventType}
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<List<EventDTO>> getEventsByTimeRange(Instant startTime, Instant endTime) {
        // 实现 GET /api/events/time-range
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<List<EventDTO>> getEventsByCorrelationId(String correlationId) {
        // 实现 GET /api/events/correlation/{correlationId}
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<EventApi.EventStats> getEventStats() {
        // 实现 GET /api/events/stats
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }
    
    // ========== TopicApi 实现 ==========

    @Override
    public ApiResponse<TopicDTO> registerTopic(TopicDTO topicDTO) {
        // 实现 POST /api/topics/register
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<List<TopicDTO>> getAllTopics() {
        // 实现 GET /api/topics
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<TopicDTO> getTopicByName(String topicName) {
        // 实现 GET /api/topics/{topicName}
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<TopicDTO> updateTopic(String topicName, String description) {
        // 实现 PUT /api/topics/{topicName}
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<Boolean> activateTopic(String topicName) {
        // 实现 POST /api/topics/{topicName}/activate
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<Boolean> deactivateTopic(String topicName) {
        // 实现 POST /api/topics/{topicName}/deactivate
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }

    @Override
    public ApiResponse<Boolean> deleteTopic(String topicName) {
        // 实现 DELETE /api/topics/{topicName}
        throw new UnsupportedOperationException("需要具体实现，建议使用RestTemplate或Feign");
    }
    
    // ========== 工具方法 ==========
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public String getTenantId() {
        return tenantId;
    }
} 