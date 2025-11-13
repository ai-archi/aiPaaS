package com.aixone.eventcenter.notification.infrastructure;

import com.aixone.eventcenter.notification.domain.Notification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 推送通知发送器
 * 简化实现，实际应该集成极光推送、个推等
 */
@Component
public class PushNotificationSender implements NotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationSender.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean send(Notification notification) {
        try {
            // 解析接收者信息
            JsonNode recipientInfo = objectMapper.readTree(notification.getRecipientInfo());
            String deviceToken = recipientInfo.has("deviceToken") ? recipientInfo.get("deviceToken").asText() : null;
            
            if (deviceToken == null || deviceToken.isEmpty()) {
                logger.warn("设备Token为空，发送失败 - NotificationId: {}", notification.getNotificationId());
                return false;
            }
            
            // 解析通知内容
            JsonNode content = objectMapper.readTree(notification.getNotificationContent());
            String title = content.has("title") ? content.get("title").asText() : "通知";
            String body = content.has("body") ? content.get("body").asText() : "";
            
            // TODO: 集成实际的推送服务（极光推送、个推等）
            // 这里只是模拟发送
            logger.info("推送发送成功（模拟） - NotificationId: {}, DeviceToken: {}, Title: {}, Body: {}", 
                    notification.getNotificationId(), deviceToken, title, body);
            
            return true;
            
        } catch (Exception e) {
            logger.error("推送发送失败 - NotificationId: {}, Error: {}", 
                    notification.getNotificationId(), e.getMessage(), e);
            return false;
        }
    }
}

