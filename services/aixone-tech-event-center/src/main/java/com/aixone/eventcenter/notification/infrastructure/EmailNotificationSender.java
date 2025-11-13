package com.aixone.eventcenter.notification.infrastructure;

import com.aixone.eventcenter.notification.domain.Notification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 邮件通知发送器
 */
@Component
public class EmailNotificationSender implements NotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationSender.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${notification.email.from:no-reply@aixone.com}")
    private String fromEmail;
    
    @Override
    public boolean send(Notification notification) {
        if (mailSender == null) {
            logger.warn("JavaMailSender未配置，邮件发送失败 - NotificationId: {}", notification.getNotificationId());
            return false;
        }
        
        try {
            // 解析接收者信息
            JsonNode recipientInfo = objectMapper.readTree(notification.getRecipientInfo());
            String toEmail = recipientInfo.has("email") ? recipientInfo.get("email").asText() : null;
            
            if (toEmail == null || toEmail.isEmpty()) {
                logger.warn("邮件地址为空，发送失败 - NotificationId: {}", notification.getNotificationId());
                return false;
            }
            
            // 解析通知内容
            JsonNode content = objectMapper.readTree(notification.getNotificationContent());
            String subject = content.has("subject") ? content.get("subject").asText() : "通知";
            String body = content.has("body") ? content.get("body").asText() : "";
            
            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            
            // 发送邮件
            mailSender.send(message);
            
            logger.info("邮件发送成功 - NotificationId: {}, To: {}", notification.getNotificationId(), toEmail);
            return true;
            
        } catch (Exception e) {
            logger.error("邮件发送失败 - NotificationId: {}, Error: {}", 
                    notification.getNotificationId(), e.getMessage(), e);
            return false;
        }
    }
}

