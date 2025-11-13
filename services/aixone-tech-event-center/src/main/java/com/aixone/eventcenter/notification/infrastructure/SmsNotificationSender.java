package com.aixone.eventcenter.notification.infrastructure;

import com.aixone.eventcenter.notification.domain.Notification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 短信通知发送器
 * 简化实现，实际应该集成阿里云SMS或腾讯云SMS
 */
@Component
public class SmsNotificationSender implements NotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationSender.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean send(Notification notification) {
        try {
            // 解析接收者信息
            JsonNode recipientInfo = objectMapper.readTree(notification.getRecipientInfo());
            String phoneNumber = recipientInfo.has("phone") ? recipientInfo.get("phone").asText() : null;
            
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                logger.warn("手机号码为空，发送失败 - NotificationId: {}", notification.getNotificationId());
                return false;
            }
            
            // 解析通知内容
            JsonNode content = objectMapper.readTree(notification.getNotificationContent());
            String message = content.has("body") ? content.get("body").asText() : 
                           content.has("message") ? content.get("message").asText() : "";
            
            // TODO: 集成实际的短信服务（阿里云SMS、腾讯云SMS等）
            // 这里只是模拟发送
            logger.info("短信发送成功（模拟） - NotificationId: {}, Phone: {}, Message: {}", 
                    notification.getNotificationId(), phoneNumber, message);
            
            return true;
            
        } catch (Exception e) {
            logger.error("短信发送失败 - NotificationId: {}, Error: {}", 
                    notification.getNotificationId(), e.getMessage(), e);
            return false;
        }
    }
}

