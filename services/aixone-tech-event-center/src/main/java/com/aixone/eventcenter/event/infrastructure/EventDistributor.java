package com.aixone.eventcenter.event.infrastructure;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.EventDeliveryRecord;
import com.aixone.eventcenter.event.domain.Subscription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 事件分发器
 * 负责将事件分发到订阅者的端点
 */
@Service
public class EventDistributor {
    private static final Logger logger = LoggerFactory.getLogger(EventDistributor.class);
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    @Autowired(required = false)
    private EventDeliveryRecordService deliveryRecordService;
    
    /**
     * 异步分发事件到订阅者
     */
    public CompletableFuture<Boolean> distributeAsync(Event event, Subscription subscription) {
        return CompletableFuture.supplyAsync(() -> {
            return distribute(event, subscription);
        }, executorService);
    }
    
    /**
     * 同步分发事件到订阅者
     * @param event 事件
     * @param subscription 订阅
     * @param deliveryRecord 分发记录（可选，用于重试）
     * @return 是否成功
     */
    public boolean distribute(Event event, Subscription subscription, EventDeliveryRecord deliveryRecord) {
        try {
            // 构建请求体
            EventNotification notification = new EventNotification();
            notification.setEventId(event.getEventId());
            notification.setEventType(event.getEventType());
            notification.setEventSource(event.getEventSource());
            notification.setEventData(event.getEventData());
            notification.setTenantId(event.getTenantId());
            notification.setCorrelationId(event.getCorrelationId());
            notification.setCreatedAt(event.getCreatedAt().toString());
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<EventNotification> request = new HttpEntity<>(notification, headers);
            
            // 发送HTTP POST请求
            ResponseEntity<String> response = restTemplate.exchange(
                    subscription.getSubscriberEndpoint(),
                    HttpMethod.POST,
                    request,
                    String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("事件分发成功 - SubscriptionId: {}, EventId: {}, Endpoint: {}", 
                        subscription.getSubscriptionId(), event.getEventId(), subscription.getSubscriberEndpoint());
                
                // 记录成功
                if (deliveryRecord != null && deliveryRecordService != null) {
                    deliveryRecord.markAsDelivered();
                    deliveryRecordService.save(deliveryRecord);
                }
                
                return true;
            } else {
                String errorMsg = String.format("HTTP状态码: %s", response.getStatusCode());
                logger.warn("事件分发失败 - SubscriptionId: {}, EventId: {}, Status: {}", 
                        subscription.getSubscriptionId(), event.getEventId(), response.getStatusCode());
                
                // 记录失败
                if (deliveryRecord != null && deliveryRecordService != null) {
                    deliveryRecord.markAsFailed(errorMsg);
                    deliveryRecordService.save(deliveryRecord);
                }
                
                return false;
            }
            
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            logger.error("事件分发异常 - SubscriptionId: {}, EventId: {}, Endpoint: {}, Error: {}", 
                    subscription.getSubscriptionId(), event.getEventId(), 
                    subscription.getSubscriberEndpoint(), errorMsg, e);
            
            // 记录失败
            if (deliveryRecord != null && deliveryRecordService != null) {
                deliveryRecord.markAsFailed(errorMsg);
                deliveryRecordService.save(deliveryRecord);
            }
            
            return false;
        }
    }
    
    /**
     * 同步分发事件到订阅者（无记录）
     */
    public boolean distribute(Event event, Subscription subscription) {
        return distribute(event, subscription, null);
    }
    
    /**
     * 事件通知DTO
     */
    public static class EventNotification {
        private Long eventId;
        private String eventType;
        private String eventSource;
        private String eventData;
        private String tenantId;
        private String correlationId;
        private String createdAt;
        
        // Getters and Setters
        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getEventSource() { return eventSource; }
        public void setEventSource(String eventSource) { this.eventSource = eventSource; }
        public String getEventData() { return eventData; }
        public void setEventData(String eventData) { this.eventData = eventData; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getCorrelationId() { return correlationId; }
        public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}

