package com.aixone.eventcenter.notification.infrastructure;

import com.aixone.eventcenter.notification.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import jakarta.annotation.PostConstruct;

/**
 * 组合通知发送器
 * 根据通知渠道选择合适的发送器
 */
@Service
public class CompositeNotificationSender implements NotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(CompositeNotificationSender.class);
    
    private final Map<Notification.NotificationChannel, NotificationSender> senders = new HashMap<>();
    
    @Autowired(required = false)
    private EmailNotificationSender emailNotificationSender;
    
    @Autowired(required = false)
    private SmsNotificationSender smsNotificationSender;
    
    @Autowired(required = false)
    private PushNotificationSender pushNotificationSender;
    
    /**
     * 初始化发送器映射
     */
    @PostConstruct
    public void initSenders() {
        if (emailNotificationSender != null) {
            senders.put(Notification.NotificationChannel.EMAIL, emailNotificationSender);
        }
        if (smsNotificationSender != null) {
            senders.put(Notification.NotificationChannel.SMS, smsNotificationSender);
        }
        if (pushNotificationSender != null) {
            senders.put(Notification.NotificationChannel.PUSH, pushNotificationSender);
        }
    }
    
    @Override
    public boolean send(Notification notification) {
        
        NotificationSender sender = senders.get(notification.getChannel());
        if (sender == null) {
            logger.warn("未找到渠道 {} 的发送器，通知发送失败 - NotificationId: {}", 
                    notification.getChannel(), notification.getNotificationId());
            return false;
        }
        
        try {
            return sender.send(notification);
        } catch (Exception e) {
            logger.error("通知发送异常 - NotificationId: {}, Channel: {}, Error: {}", 
                    notification.getNotificationId(), notification.getChannel(), e.getMessage(), e);
            return false;
        }
    }
}

